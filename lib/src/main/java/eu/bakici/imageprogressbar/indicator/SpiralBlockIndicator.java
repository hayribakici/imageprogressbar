package eu.bakici.imageprogressbar.indicator;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import static android.graphics.Color.*;

public class SpiralBlockIndicator extends BlockIndicator {

    Point center;

    int[] colors = {
            BLACK, BLUE, RED, YELLOW, MAGENTA, CYAN, DKGRAY, GRAY, GREEN,
            LTGRAY, WHITE
    };

    int colorIndex = 0;

    private int getNextColor() {
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }


    @Override
    protected void onPostBlockInitialization() {
        center = getCenterPoint();

        Bitmap bitmap = Bitmap.createBitmap(mPreBitmap.getWidth(), mPreBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        final int numberOfCols = (mWidth / mPixels) + 1;
        final int numberOfRows = (mHeight / mPixels) + 1;
        final int numberOfCircles = numberOfCols / 2;
        final int numberOfDivisions = numberOfRows / 2;


//        Paint paint = new Paint();
//        for (int i = 0; i < mBlockSum; i++) {
//            final int col = i % numberOfCols;
//            final int row = i / numberOfCols;
//
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawCircle(center.x, center.y, row * mPixels, paint);
//
//
//
//            if (row > numberOfCircles) {
//                break;
//            }
//        }

        float x0 = center.x;
        float y0 = center.y;
        for (int i = 0; i < mPixels * 360; i++) {
            double angle = 0.1 * i;
            float x = (float) (center.x + (10 * angle) * Math.cos(angle));
            float y = (float) (center.y + (10 * angle) * Math.sin(angle));

            canvas.drawLine(x0, y0, x, y, new Paint());
            x0 = x;
            y0 = y;
        }

//        canvas.drawPath(path, new Paint());
//
//        int lastAngle = 0;
//        for (int i = 1; i <= numberOfCols; i++) {
//            int angle = i * (360 / numberOfCols);
//
//            for (int j = lastAngle; j < angle; j++) {
//                int x1 = (int) (center.x + (i * mPixels) * Math.cos(j));
//                int y1 = (int) (center.y + (i * mPixels) * Math.sin(j));
//                Log.d("SpiralBlockIndicator", String.format("j = %s, lastAngle = %s, angle = %s, x1 = %s, y1 = %s", j, lastAngle, angle, x1, y1));
//                canvas.drawPoint(x1, y1, new Paint());
//            }
////            int x = (int) (center.x + (i * mPixels) * Math.cos(Math.toRadians(angle)));
////            int y = (int) (center.y + (i * mPixels) * Math.sin(Math.toRadians(angle)));
//            lastAngle = angle;
//
//            Log.d("SpiralBlockIndicator", String.format("i = %s, angle = %s", i, angle));
//
////            canvas.drawPoint();
////            canvas.drawLine(center.x, center.y, x, y, new Paint());
////            RectF oval = new RectF(x, y, lastRight, lastBottom);
////            final Paint rectPaint = new Paint();
////            rectPaint.setStyle(Paint.Style.STROKE);
////            canvas.drawRect(oval, rectPaint);
//
//
//        }

        mPreBitmap = bitmap;
        mCurrentBitmap = bitmap;
    }
    private Point getCenterPoint() {
        final int centerX = mWidth / 2;
        final int centerY = mHeight / 2;
        return new Point(centerX, centerY);
    }
}
