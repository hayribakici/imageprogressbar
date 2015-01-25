package com.thehayro.view.indicator;

import com.thehayro.view.utils.IndicatorUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class SpiralBlockIndicator extends BlockIndicator {


    Rect mCenterBlock;

    Point center;

    @Override
    protected void onPostBlockInitialization() {
        final int centerX = mWidth / 2;
        final int centerY = mHeight / 2;
        for (int i = 0; i < mBlockSum; i++) {
            Rect block = mBlocks.get(i);
            if (block.contains(centerX, centerY)) {
                mCenterBlock = block;
                break;
            }
        }
        center = new Point(centerX, centerY);
    }

    @Override
    public void onProgress(final Bitmap source, final int progressPercent, final OnProgressIndicationUpdatedListener callback) {
        int blockPosOfPercent = IndicatorUtils.calcPercent(mBlockSum, progressPercent);
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Path path = new Path();


        callback.onProgressIndicationUpdated(bitmap);
    }

    /**
     *
     * @param xStart vector start point
     * @param yStart
     * @param xEnd vector end point
     * @param yEnd
     * @param ovalRectOUT RectF to store result
     * @param direction left even number, right odd number.
     * @return start angle
     */
    public static float getSemicircle(float xStart, float yStart, float xEnd,
                                      float yEnd, RectF ovalRectOUT, int direction) {

        float centerX = xStart + ((xEnd - xStart) / 2);
        float centerY = yStart + ((yEnd - yStart) / 2);

        double xLen = (xEnd - xStart);
        double yLen = (yEnd - yStart);
        float radius = (float) (Math.sqrt(xLen * xLen + yLen * yLen) / 2);

        RectF oval = new RectF((float) (centerX - radius),
            (float) (centerY - radius), (float) (centerX + radius),
            (float) (centerY + radius));

        ovalRectOUT.set(oval);

        double radStartAngle = 0;
        if (direction % 2 == 0) {
            radStartAngle = Math.atan2(yStart - centerY, xStart - centerX);
        } else {
            radStartAngle = Math.atan2(yEnd - centerY, xEnd - centerX);
        }

        return (float) Math.toDegrees(radStartAngle);

    }

}
