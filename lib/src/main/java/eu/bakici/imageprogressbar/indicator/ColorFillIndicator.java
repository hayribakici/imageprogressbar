package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class ColorFillIndicator extends ProgressIndicator {

    /**
     * Type of how the image will be processed.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT,
            PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT,
            PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP,
            PROGRESS_DIRECTION_VERTICAL_TOP_DOWN
    })
    public @interface ProgressDirection {
    }

    /**
     * Lets the progress indication go from left to right.
     */
    public final static int PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT = 0;

    /**
     * Lets the progress indication go from right to left.
     */
    public final static int PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT = 1;

    /**
     * Lets the progress indication go from top to bottom.
     */
    public final static int PROGRESS_DIRECTION_VERTICAL_TOP_DOWN = 2;

    /**
     * Lets the progress indication go from bottom to top.
     */
    public final static int PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP = 3;

    @ProgressDirection
    private int mProgressDirection;

    public ColorFillIndicator(@ProgressDirection int direction) {
        super(SYNC);
        mProgressDirection = direction;
    }


    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        final Bitmap bw = IndicatorUtils.convertGrayscale(originalBitmap);
        mPreBitmap = bw;
        mCurrentBitmap = bw;
    }

    @Override
    public void onProgress(final Bitmap originalBitmap, @IntRange(from = 0, to = 100)int progressPercent) {

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

        canvas.drawBitmap(mPreBitmap, bitmapBWRect, bitmapBWRect, normalPaint);
        canvas.drawBitmap(originalBitmap, bitmapSourceRect, bitmapSourceRect, normalPaint);
        mCurrentBitmap = output;
    }
}
