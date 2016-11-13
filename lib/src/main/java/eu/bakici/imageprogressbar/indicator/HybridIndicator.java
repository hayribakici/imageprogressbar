package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;

public class HybridIndicator extends ProgressIndicator {


    public HybridIndicator() {
        super(HYBRID);
    }

    @Override
    public final void onProgress(final Bitmap originalBitmap, final int progressPercent) {

    }



    public void onProgress(final Bitmap source, int progressPercent, final OnProgressIndicationUpdatedListener listener) {
        throw new UnsupportedOperationException("onProgress is not implemented");
    }
}
