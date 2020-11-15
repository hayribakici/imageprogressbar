package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.IntRange;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class SpiralBlockIndicator extends BlockIndicator {

    Point center;
    private int mWidth;
    private int mHeight;

    private List<Point> mPoints = new ArrayList<>();

    private List<Rect> mBlocks2 = new ArrayList<>();


    @Override
    protected void onPostBlockInitialization() {
        center = getCenterPoint();

        Bitmap bitmap = Bitmap.createBitmap(mPreBitmap.getWidth(), mPreBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();
        final int numberOfCols = (mWidth / mPixels) + 1;

        final int numberOfCircles = numberOfCols / 2;


        Log.d("spiralblock", "numberOfCols = " + numberOfCols);
        Paint paint = new Paint();
        // y axis
        canvas.drawLine(center.x, 0, center.x, mHeight, paint);
        // x axis
        canvas.drawLine(0, center.y, mWidth, center.y, paint);
        // diagonal 1
        canvas.drawLine(0, 0, mWidth, mHeight, paint);
        // diagonal 2
        canvas.drawLine(mWidth, 0, 0, mHeight, paint);

        for (int i = 0; i < numberOfCircles; i++) {
            Log.d("spiralblock", "Drawing circle radius = " + i * mPixels + " i = " + i);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(center.x, center.y, i * mPixels, paint);
        }

        float x0 = center.x;
        float y0 = center.y;
        Paint redPaint = new Paint();
        for (int i = 0; i < numberOfCols * 360; i++) {
            double angle = 0.1 * i;
            float x = (float) (center.x + (numberOfCols * angle) * Math.cos(angle));
            float y = (float) (center.y + (numberOfCols * angle) * Math.sin(angle));

//            mPoints.add(new Point(Math.round(x), Math.round(y)));
            Rect rectFromPosition = mBlockHelper.getRectFromPosition(Math.round(x), Math.round(y));
            if (rectFromPosition != null) {
                mBlocks2.add(rectFromPosition);
            } else {
                Log.w("spiralblock", String.format("rect in (%s,%s) not found", Math.round(x), Math.round(y)));
            }

            canvas.drawLine(x0, y0, x, y, redPaint);
            x0 = x;
            y0 = y;
        }
    }

    @Override
    public void onProgress(Bitmap source, @IntRange(from = 0, to = 100) int progressPercent, OnProgressIndicationUpdatedListener listener) {
        final int height = source.getHeight();
        final int width = source.getWidth();
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        int blockPosOfPercent = IndicatorUtils.calcPercent(mBlocks2.size(), progressPercent) + 1;
        addColorBlockToBitmap(source, canvas, blockPosOfPercent - 1);
        mPreBitmap.recycle();
        mPreBitmap = output;
        listener.onProgressIndicationUpdated(output);
    }

    private void addColorBlockToBitmap(final Bitmap originalBitmap, final Canvas canvas, final int blockPos) {
        List<Rect> blocks = mBlocks2;
        if (blockPos >= blocks.size()) {
            return;
        }
        final Rect randomBlock = blocks.get(blockPos);
        final Paint paint = new Paint();
        canvas.drawBitmap(mPreBitmap, 0, 0, paint);
        canvas.drawBitmap(originalBitmap, randomBlock, randomBlock, paint);
    }

    private Point getCenterPoint() {
        final int centerX = mWidth / 2;
        final int centerY = mHeight / 2;
        return new Point(centerX, centerY);
    }
}
