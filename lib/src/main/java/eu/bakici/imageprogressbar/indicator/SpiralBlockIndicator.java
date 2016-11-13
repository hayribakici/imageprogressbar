package eu.bakici.imageprogressbar.indicator;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class SpiralBlockIndicator extends BlockIndicator {

    Point center;



    @Override
    protected void onPostBlockInitialization() {
        center = getCenterPoint();

        Bitmap bitmap = Bitmap.createBitmap(mPreBitmap.getWidth(), mPreBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        final int numberOfCols = (mWidth / mPixels) + 1;
        final int numberOfRows = (mHeight / mPixels) + 1;
        final int numberOfCircles = numberOfCols / 2;
        final int numberOfDivisions = numberOfRows / 2;


        for (int i = 0; i < mBlockSum; i++) {
            final int col = i % numberOfCols;
            final int row = i / numberOfCols;

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(center.x, center.y, row * mPixels, paint);



            if (row > numberOfCircles) {
                break;
            }
        }
        Path path = new Path();
        int lastTop = 0;
        int lastLeft = 0;
        int lastRight = center.x;
        int lastBottom = center.y;
        for (int i = 0; i < numberOfCols; i++) {
            int angle = i * (360 / numberOfCols);
            final int col = i % numberOfCols;
            int x = (int) (center.x + (col * mPixels) * Math.cos(Math.toRadians(angle)));
            int y = (int) (center.y + (col * mPixels) * Math.sin(Math.toRadians(angle)));


            canvas.drawLine(center.x, center.y, x, y, new Paint());
            RectF oval = new RectF(x, y, lastRight, lastBottom);
            final Paint rectPaint = new Paint();
            rectPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(oval, rectPaint);


        }

        mPreBitmap = bitmap;
        mCurrentBitmap = bitmap;
    }
    private Point getCenterPoint() {
        final int centerX = mWidth / 2;
        final int centerY = mHeight / 2;
        return new Point(centerX, centerY);
    }
}
