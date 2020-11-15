package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import androidx.annotation.IntRange;

public class HybridIndicator extends ProgressIndicator {


    public HybridIndicator() {
        super(HYBRID);
    }

    @Override
    public final void onProgress(final Bitmap originalBitmap, int progressPercent) {
        // onProgress(Bitmap, progressPercent, listener) is used
    }



    public void onProgress(final Bitmap source, @IntRange(from = 0, to = 100) int progressPercent, final OnProgressIndicationUpdatedListener listener) {
        throw new UnsupportedOperationException("onProgress is not implemented");
    }
}
