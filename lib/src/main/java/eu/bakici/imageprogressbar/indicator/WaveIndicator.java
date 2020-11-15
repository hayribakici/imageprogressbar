// mostly taken from https://github.com/tangqi92/WaveLoadingView
package eu.bakici.imageprogressbar.indicator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class WaveIndicator extends ProgressIndicator {


    /**
     * +------------------------+
     * | wave length - 波长      |__________
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude - 振幅
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|_______
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level - 水位
     * |                        |  |
     * |                        |  |
     * +------------------------+__|_______
     */
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.1f;
    private static final float DEFAULT_AMPLITUDE_VALUE = 50.0f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;
    private static final int DEFAULT_WAVE_PROGRESS_VALUE = 50;
    private static final int DEFAULT_WAVE_COLOR = Color.parseColor("#212121");
    private static final int DEFAULT_WAVE_BACKGROUND_COLOR = Color.parseColor("#00000000");
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#212121");
    private static final int DEFAULT_STROKE_COLOR = Color.TRANSPARENT;
    private static final float DEFAULT_BORDER_WIDTH = 0;
    private static final float DEFAULT_TITLE_STROKE_WIDTH = 0;
    // This is incorrect/not recommended by Joshua Bloch in his book Effective Java (2nd ed).
    private static final int DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE.ordinal();
    private static final int DEFAULT_TRIANGLE_DIRECTION = TriangleDirection.NORTH.ordinal();
    private static final int DEFAULT_ROUND_RECTANGLE_X_AND_Y = 30;
    private static final float DEFAULT_TITLE_TOP_SIZE = 18.0f;
    private static final float DEFAULT_TITLE_CENTER_SIZE = 22.0f;
    private static final float DEFAULT_TITLE_BOTTOM_SIZE = 18.0f;

    public enum ShapeType {
        TRIANGLE,
        CIRCLE,
        SQUARE,
        RECTANGLE
    }

    public enum TriangleDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    // Dynamic Properties.
    private int mCanvasSize;
    private int mCanvasHeight;
    private int mCanvasWidth;
    private float mAmplitudeRatio;
    private int mWaveBgColor;
    private int mWaveColor;
    private int mShapeType;
    private int mTriangleDirection;
    private int mRoundRectangleXY;

    // Properties.
    private String mTopTitle;
    private String mCenterTitle;
    private String mBottomTitle;
    private float mDefaultWaterLevel;
    private float mWaterLevelRatio = 1f;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;
    private int mProgressValue = DEFAULT_WAVE_PROGRESS_VALUE;
    private boolean mIsRoundRectangle;

    // Object used to draw.
    // Shader containing repeated waves.
    private BitmapShader mWaveShader;
    private Bitmap bitmapBuffer;
    // Shader matrix.
    private Matrix mShaderMatrix;
    // Paint to draw wave.
    private Paint mWavePaint;
    //Paint to draw waveBackground.
    private Paint mWaveBgPaint;


    // Animation.
    private ObjectAnimator waveShiftAnim;
    private AnimatorSet mAnimatorSet;

    private Context mContext;

    // Constructor & Init Method.
    public WaveIndicator(final Context context) {
        super(ProgressIndicator.SYNC);
        this.mContext = context;
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        // Init Wave.
        mShaderMatrix = new Matrix();
        mWavePaint = new Paint();
        // The ANTI_ALIAS_FLAG bit AntiAliasing smooths out the edges of what is being drawn,
        // but is has no impact on the interior of the shape.
        mWavePaint.setAntiAlias(true);
        mWaveBgPaint = new Paint();
        mWaveBgPaint.setAntiAlias(true);
        // Init Animation
        initAnimation();

        // Load the styled attributes and set their properties

        // Init ShapeType
        mShapeType = DEFAULT_WAVE_SHAPE;

        // Init Wave
        mWaveColor = DEFAULT_WAVE_COLOR;
        mWaveBgColor = DEFAULT_WAVE_BACKGROUND_COLOR;

        mWaveBgPaint.setColor(mWaveBgColor);

        // Init AmplitudeRatio

        mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;


        // Init Triangle direction
        mTriangleDirection = DEFAULT_TRIANGLE_DIRECTION;
    }

    public WaveIndicator(int indicationProcess) {
        super(indicationProcess);
    }

    @Override
    public void onPreProgress(Bitmap originalBitmap) {
//        mPreBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
//        mCurrentBitmap = mPreBitmap;
    }

    @Override
    public synchronized void onProgress(Bitmap originalBitmap, int progressPercent) {
        Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        mCanvasSize = originalBitmap.getWidth();
        if (originalBitmap.getHeight() < mCanvasSize) {
            mCanvasSize = originalBitmap.getHeight();
        }
        // Draw Wave.
        // Modify paint shader according to mShowWave state.
        if (mWaveShader != null) {
            // First call after mShowWave, assign it to our paint.
            if (mWavePaint.getShader() == null) {
                mWavePaint.setShader(mWaveShader);
            }

            // Sacle shader according to waveLengthRatio and amplitudeRatio.
            // This decides the size(waveLengthRatio for width, amplitudeRatio for height) of waves.
            mShaderMatrix.setScale(1, mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0, mDefaultWaterLevel);
            // Translate shader according to waveShiftRatio and waterLevelRatio.
            // This decides the start position(waveShiftRatio for x, waterLevelRatio for y) of waves.
            mShaderMatrix.postTranslate(mWaveShiftRatio * originalBitmap.getWidth(),
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * originalBitmap.getHeight());

            // Assign matrix to invalidate the shader.
            mWaveShader.setLocalMatrix(mShaderMatrix);


            // The default type is triangle.
            switch (mShapeType) {
                // Draw triangle
                case 0:
                    // Currently does not support the border settings
                    Point start = new Point(0, originalBitmap.getHeight());
                    Path triangle = getEquilateralTriangle(start, originalBitmap.getWidth(), originalBitmap.getHeight(), mTriangleDirection);
                    canvas.drawPath(triangle, mWaveBgPaint);
                    canvas.drawPath(triangle, mWavePaint);
                    break;
                // Draw circle
                /** case 1:


                 float radius = getWidth() / 2f ;
                 // Draw background
                 canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mWaveBgPaint);
                 canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mWavePaint);
                 break;
                 // Draw square
                 case 2:
                 if (borderWidth > 0) {
                 canvas.drawRect(
                 borderWidth / 2f,
                 borderWidth / 2f,
                 getWidth() - borderWidth / 2f - 0.5f,
                 getHeight() - borderWidth / 2f - 0.5f,
                 mBorderPaint);
                 }

                 canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                 getHeight() - borderWidth, mWaveBgPaint);
                 canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                 getHeight() - borderWidth, mWavePaint);
                 break;
                 // Draw rectangle
                 case 3:
                 if (mIsRoundRectangle) {
                 if (borderWidth > 0) {
                 RectF rect = new RectF(borderWidth / 2f, borderWidth / 2f, getWidth() - borderWidth / 2f - 0.5f, getHeight() - borderWidth / 2f - 0.5f);
                 canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mBorderPaint);
                 canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWaveBgPaint);
                 canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWavePaint);
                 } else {
                 RectF rect = new RectF(0, 0, getWidth(), getHeight());
                 canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWaveBgPaint);
                 canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWavePaint);
                 }
                 } else {
                 if (borderWidth > 0) {
                 canvas.drawRect(borderWidth / 2f, borderWidth / 2f, getWidth() - borderWidth / 2f - 0.5f, getHeight() - borderWidth / 2f - 0.5f, mWaveBgPaint);
                 canvas.drawRect(borderWidth / 2f, borderWidth / 2f, getWidth() - borderWidth / 2f - 0.5f, getHeight() - borderWidth / 2f - 0.5f, mWavePaint);
                 } else {
                 canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mWaveBgPaint);
                 canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mWavePaint);
                 }
                 }
                 break;*/
                default:
                    break;
            }
        } else {
            mWavePaint.setShader(null);
        }
        canvas.drawBitmap(originalBitmap, 0, 0, mWavePaint);
        setProgressValue(progressPercent);
        mCurrentBitmap = output;
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        cancelAnimation();
    }


//
//
//    private void updateWaveShader() {
//        // IllegalArgumentException: width and height must be > 0 while loading Bitmap from View
//        // http://stackoverflow.com/questions/17605662/illegalargumentexception-width-and-height-must-be-0-while-loading-bitmap-from
//        if (bitmapBuffer == null || haveBoundsChanged()) {
//            if (bitmapBuffer != null)
//                bitmapBuffer.recycle();
//            int width = getMeasuredWidth();
//            int height = getMeasuredHeight();
//            if (width > 0 && height > 0) {
//                double defaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width;
//                float defaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO;
//                mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO;
//                float defaultWaveLength = width;
//
//                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//
//                Paint wavePaint = new Paint();
//                wavePaint.setStrokeWidth(2);
//                wavePaint.setAntiAlias(true);
//
//                // Draw default waves into the bitmap.
//                // y=Asin(ωx+φ)+h
//                final int endX = width + 1;
//                final int endY = height + 1;
//
//                float[] waveY = new float[endX];
//
//                wavePaint.setColor(adjustAlpha(mWaveColor, 0.3f));
//                for (int beginX = 0; beginX < endX; beginX++) {
//                    double wx = beginX * defaultAngularFrequency;
//                    float beginY = (float) (mDefaultWaterLevel + defaultAmplitude * Math.sin(wx));
//                    canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);
//                    waveY[beginX] = beginY;
//                }
//
//                wavePaint.setColor(mWaveColor);
//                final int wave2Shift = (int) (defaultWaveLength / 4);
//                for (int beginX = 0; beginX < endX; beginX++) {
//                    canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
//                }
//
//                // Use the bitamp to create the shader.
//                mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
//                this.mWavePaint.setShader(mWaveShader);
//            }
//        }
//    }
//
//    private boolean haveBoundsChanged() {
//        return getMeasuredWidth() != bitmapBuffer.getWidth() ||
//                getMeasuredHeight() != bitmapBuffer.getHeight();
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = measureWidth(widthMeasureSpec);
//        int height = measureHeight(heightMeasureSpec);
//        // If shapType is rectangle
//        if (getShapeType() == 3) {
//            setMeasuredDimension(width, height);
//        } else {
//            int imageSize = (width < height) ? width : height;
//            setMeasuredDimension(imageSize, imageSize);
//        }
//
//    }
//
//    private int measureWidth(int measureSpec) {
//        int result;
//        int specMode = MeasureSpec.getMode(measureSpec);
//        int specSize = MeasureSpec.getSize(measureSpec);
//
//        if (specMode == MeasureSpec.EXACTLY) {
//            // The parent has determined an exact size for the child.
//            result = specSize;
//        } else if (specMode == MeasureSpec.AT_MOST) {
//            // The child can be as large as it wants up to the specified size.
//            result = specSize;
//        } else {
//            // The parent has not imposed any constraint on the child.
//            result = mCanvasWidth;
//        }
//        return result;
//    }
//
//    private int measureHeight(int measureSpecHeight) {
//        int result;
//        int specMode = MeasureSpec.getMode(measureSpecHeight);
//        int specSize = MeasureSpec.getSize(measureSpecHeight);
//
//        if (specMode == MeasureSpec.EXACTLY) {
//            // We were told how big to be.
//            result = specSize;
//        } else if (specMode == MeasureSpec.AT_MOST) {
//            // The child can be as large as it wants up to the specified size.
//            result = specSize;
//        } else {
//            // Measure the text (beware: ascent is a negative number).
//            result = mCanvasHeight;
//        }
//        return (result + 2);
//    }


//    public void setWaveBgColor(int color) {
//        this.mWaveBgColor = color;
//        mWaveBgPaint.setColor(this.mWaveBgColor);
//        updateWaveShader();
//        invalidate();
//    }
//
//    public int getWaveBgColor() {
//        return mWaveBgColor;
//    }
//
//    public void setWaveColor(int color) {
//        mWaveColor = color;
//        // Need to recreate shader when color changed ?
////        mWaveShader = null;
//        updateWaveShader();
//        invalidate();
//    }
//
//    public int getWaveColor() {
//        return mWaveColor;
//    }
//
//    public void setBorderWidth(float width) {
//        mBorderPaint.setStrokeWidth(width);
//        invalidate();
//    }
//
//    public float getBorderWidth() {
//        return mBorderPaint.getStrokeWidth();
//    }
//
//    public void setBorderColor(int color) {
//        mBorderPaint.setColor(color);
//        updateWaveShader();
//        invalidate();
//    }
//
//    public int getBorderColor() {
//        return mBorderPaint.getColor();
//    }
//
//    public void setShapeType(ShapeType shapeType) {
//        mShapeType = shapeType.ordinal();
//        invalidate();
//    }
//
//    public int getShapeType() {
//        return mShapeType;
//    }
//
//    /**
//     * Set vertical size of wave according to amplitudeRatio.
//     *
//     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
//     */
//    public void setAmplitudeRatio(int amplitudeRatio) {
//        if (this.mAmplitudeRatio != (float) amplitudeRatio / 1000) {
//            this.mAmplitudeRatio = (float) amplitudeRatio / 1000;
//            invalidate();
//        }
//    }
//
//    public float getAmplitudeRatio() {
//        return mAmplitudeRatio;
//    }

    /**
     * Water level increases from 0 to the value of WaveView.
     *
     * @param progress Default to be 50.
     */
    public void setProgressValue(int progress) {
        mProgressValue = progress;
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(this, "waterLevelRatio", mWaterLevelRatio, ((float) mProgressValue / 100));
        waterLevelAnim.setDuration(1000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        AnimatorSet animatorSetProgress = new AnimatorSet();
        animatorSetProgress.play(waterLevelAnim);
        animatorSetProgress.start();
    }

    public int getProgressValue() {
        return mProgressValue;
    }

    public void setWaveShiftRatio(float waveShiftRatio) {
        if (this.mWaveShiftRatio != waveShiftRatio) {
            this.mWaveShiftRatio = waveShiftRatio;
//            invalidate();
        }
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    public void setWaterLevelRatio(float waterLevelRatio) {
        if (this.mWaterLevelRatio != waterLevelRatio) {
            this.mWaterLevelRatio = waterLevelRatio;
//            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }


    public void startAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    public void endAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }

    public void cancelAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public void pauseAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mAnimatorSet != null) {
                mAnimatorSet.pause();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public void resumeAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mAnimatorSet != null) {
                mAnimatorSet.resume();
            }
        }
    }

    /**
     * Sets the length of the animation. The default duration is 1000 milliseconds.
     *
     * @param duration The length of the animation, in milliseconds.
     */
    public void setAnimDuration(long duration) {
        waveShiftAnim.setDuration(duration);
    }

    private void initAnimation() {
        // Wave waves infinitely.
        waveShiftAnim = ObjectAnimator.ofFloat(this, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(waveShiftAnim);
    }


    /**
     * Transparent the given color by the factor
     * The more the factor closer to zero the more the color gets transparent
     *
     * @param color  The color to transparent
     * @param factor 1.0f to 0.0f
     * @return int - A transplanted color
     */
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Paint.setTextSize(float textSize) default unit is px.
     *
     * @param spValue The real size of text
     * @return int - A transplanted sp
     */
    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Draw EquilateralTriangle
     *
     * @param p1        Start point
     * @param width     The width of triangle
     * @param height    The height of triangle
     * @param direction The direction of triangle
     * @return Path
     */
    private Path getEquilateralTriangle(Point p1, int width, int height, int direction) {
        Point p2 = null, p3 = null;
        // NORTH
        if (direction == 0) {
            p2 = new Point(p1.x + width, p1.y);
            p3 = new Point(p1.x + (width / 2), (int) (height - Math.sqrt(3.0) / 2 * height));
        }
        // SOUTH
        else if (direction == 1) {
            p2 = new Point(p1.x, p1.y - height);
            p3 = new Point(p1.x + width, p1.y - height);
            p1.x = p1.x + (width / 2);
            p1.y = (int) (Math.sqrt(3.0) / 2 * height);
        }
        // EAST
        else if (direction == 2) {
            p2 = new Point(p1.x, p1.y - height);
            p3 = new Point((int) (Math.sqrt(3.0) / 2 * width), p1.y / 2);
        }
        // WEST
        else if (direction == 3) {
            p2 = new Point(p1.x + width, p1.y - height);
            p3 = new Point(p1.x + width, p1.y);
            p1.x = (int) (width - Math.sqrt(3.0) / 2 * width);
            p1.y = p1.y / 2;
        }

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }
}
