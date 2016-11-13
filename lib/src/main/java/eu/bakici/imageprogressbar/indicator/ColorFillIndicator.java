package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class ColorFillIndicator extends ProgressIndicator {

    public final static int PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT = 0;

    public final static int PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT = 1;

    public final static int PROGRESS_DIRECTION_VERTICAL_TOP_DOWN = 2;

    public final static int PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP = 3;

    private int mProgressDirection;

    private Bitmap mBWBitmap;

    public ColorFillIndicator(final int direction) {
        super(SYNC);
        mProgressDirection = direction;
    }


    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        final Bitmap bw = createBWBitmap(originalBitmap, originalBitmap.getHeight(), originalBitmap.getWidth());
        mBWBitmap = bw;
        mCurrentBitmap = bw;
    }

    @Override
    public void onProgress(final Bitmap originalBitmap, int progressPercent) {

        final int bitmapHeight = originalBitmap.getHeight();
        final int bitmapWidth = originalBitmap.getWidth();
        final int heightPercent = IndicatorUtils.calcPercent(bitmapHeight, progressPercent);
        final int widthPercent = IndicatorUtils.calcPercent(bitmapWidth, progressPercent);


        Rect bitmapBWRect;
        Rect bitmapSourceRect;
        switch (mProgressDirection) {
            case PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT:
                bitmapSourceRect = new Rect(0, 0, widthPercent, bitmapHeight);
                bitmapBWRect = new Rect(widthPercent, 0, bitmapWidth, bitmapHeight);
                break;
            case PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT:
                final int complementWidthPercent = bitmapWidth - widthPercent;
                bitmapSourceRect = new Rect(complementWidthPercent, 0, bitmapWidth, bitmapHeight);
                bitmapBWRect = new Rect(0, 0, complementWidthPercent, bitmapHeight);
                break;
            case PROGRESS_DIRECTION_VERTICAL_TOP_DOWN:
                bitmapSourceRect = new Rect(0, 0, bitmapWidth, heightPercent);
                bitmapBWRect = new Rect(0, heightPercent, bitmapWidth, bitmapHeight);
                break;
            case PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP:
                final int complementHeightPercent = bitmapHeight - heightPercent;
                bitmapSourceRect = new Rect(0, complementHeightPercent, bitmapWidth, bitmapHeight);
                bitmapBWRect = new Rect(0, 0, bitmapWidth, complementHeightPercent);
                break;
            default:
                throw new IllegalArgumentException("no valid progress direction specified");
        }

        final Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(output);
        final Paint normalPaint = new Paint();

        canvas.drawBitmap(mBWBitmap, bitmapBWRect, bitmapBWRect, normalPaint);
        canvas.drawBitmap(originalBitmap, bitmapSourceRect, bitmapSourceRect, normalPaint);
        mCurrentBitmap = output;
    }

    private Bitmap createBWBitmap(final Bitmap source, final int height, final int width) {
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(source, 0, 0, paint);
        return output;
    }

}
