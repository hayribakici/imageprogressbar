package com.thehayro.view.indicator;

import android.graphics.Bitmap;

/**
 * Base class for Progress indication.
 */
public abstract class ProgressIndicator {

    static final String TAG = ProgressIndicator.class.getSimpleName();

    protected Bitmap mCurrentBitmap;

    /**
     * The type of processing this indicator is running on.
     */
    private IndicationProcessingType mIndicationProcess;

    /**
     * Standard constructor. Initializes a ProgressIndicator instance.
     * @param indicationProcess the type of processing this indicator should have.
     */
    public ProgressIndicator(final IndicationProcessingType indicationProcess) {
        mIndicationProcess = indicationProcess;
    }

    /**
     * Called once at the beginning before the action progress is called. This method
     * allows for instance to do some Bitmap manipulation before the progress starts.
     * @param originalBitmap the
     */
    public void onPreProgress(final Bitmap originalBitmap) {
        throw new UnsupportedOperationException("onPreProgress is not implemented");
    }


    public void onProgress(final Bitmap originalBitmap, int progressPercent) {
        throw new UnsupportedOperationException("onProgress is not implemented");
    }

    public Bitmap getCurrentBitmap() {
        return mCurrentBitmap;
    }

    public void cleanUp() {
        mCurrentBitmap = null;
    }

    public IndicationProcessingType getIndicationProcessingType() {
        return mIndicationProcess;
    }

    public enum IndicationProcessingType {
        SYNC,
        ASYNC,
        HYBRID
    }

    public static interface OnProgressIndicationUpdatedListener {
        void onProgressIndicationUpdated(final Bitmap bitmap);
    }
}
