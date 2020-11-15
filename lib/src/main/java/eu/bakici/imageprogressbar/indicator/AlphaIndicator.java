package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.IntRange;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

/**
 * Created on 13.11.16.
 */

public class AlphaIndicator extends ProgressIndicator {

    private static final int MAX_ALPHA = 255;

    public AlphaIndicator() {
        super(SYNC);
    }

    @Override
    public void onPreProgress(Bitmap originalBitmap) {
        mPreBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        mCurrentBitmap = mPreBitmap;
    }

    @Override
    public synchronized void onProgress(Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        final Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(IndicatorUtils.calcPercent(MAX_ALPHA, progressPercent));
        canvas.drawBitmap(mPreBitmap, 0, 0, new Paint());
        canvas.drawBitmap(originalBitmap, 0,0, alphaPaint);
        mCurrentBitmap = output;
    }
}
