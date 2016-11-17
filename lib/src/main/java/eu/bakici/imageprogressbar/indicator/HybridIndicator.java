package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.support.annotation.IntRange;

/**
 * An indicator that is a synchronous indicator at its core, but does now and then gives asynchronous
 * callbacks. This is good, if the progress percents becomes jumpy, meaning there is not linear
 * increase of the progress. This indicator is there to 'fill the gaps' and let progression
 * catch on from where the progress jump started until its desired progression.
 */
public class HybridIndicator extends ProgressIndicator {


    public HybridIndicator() {
        super(HYBRID);
    }

    @Override
    public final void onProgress(final Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        throw new UnsupportedOperationException("onProgress is not implemented");
        // onProgress(Bitmap, progressPercent, listener) is used
    }


    /**
     * Same as {@link #onProgress(Bitmap, int)} but with a callback.
     * @param originalBitmap the original bitmap.
     * @param progressPercent the percentage of the current progress.
     * @param listener a callback listener for filling the gaps between progress jumps.
     */
    public void onProgress(final Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent, final OnProgressIndicationUpdatedListener listener) {
        throw new UnsupportedOperationException("onProgress is not implemented");
    }

    /**
     * Callback interface when the indication has been updated.
     */
    public interface OnProgressIndicationUpdatedListener {
        void onProgressIndicationUpdated(final Bitmap bitmap);
    }
}
