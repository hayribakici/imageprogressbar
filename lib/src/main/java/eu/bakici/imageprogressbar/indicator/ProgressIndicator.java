package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;

/**
 * Base class for Progress indication.
 */
public abstract class ProgressIndicator {

    static final String TAG = ProgressIndicator.class.getSimpleName();


    /**
     * The current bitmap the view is displaying.
     */
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


    /**
     * Called when the progress bar is moving.
     * @param originalBitmap the original bitmap
     * @param progressPercent the values in percent. Goes from 0 to 100
     */
    public void onProgress(final Bitmap originalBitmap, int progressPercent) {
        throw new UnsupportedOperationException("onProgress is not implemented");
    }

    /**
     * The current displayed bitmap.
     * @return the current bitmap.
     */
    public Bitmap getCurrentBitmap() {
        return mCurrentBitmap;
    }

    /**
     * Should be called when the indication is done.
     */
    public void cleanUp() {
        mCurrentBitmap = null;
    }

    /**
     *
     * @return The indicator processing type this indicator is running on.
     * @see IndicationProcessingType
     */
    public IndicationProcessingType getIndicationProcessingType() {
        return mIndicationProcess;
    }

    /**
     *
     */
    public enum IndicationProcessingType {
        SYNC,
        ASYNC,
        HYBRID
    }

    /**
     * Callback interface when
     */
    public static interface OnProgressIndicationUpdatedListener {
        void onProgressIndicationUpdated(final Bitmap bitmap);
    }
}
