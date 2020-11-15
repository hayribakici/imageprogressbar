package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Base class for Progress indication.
 */
public abstract class ProgressIndicator {

    static final String TAG = ProgressIndicator.class.getSimpleName();

    /**
     * Type of how the image will be processed.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            SYNC,
            ASYNC,
            HYBRID
    })
    public @interface IndicationProcessingType {
    }

    /**
     * Synchronous processing, the image processing will be done
     * on the main thread.
     */
    public static final int SYNC = 1;

    /**
     * Ansynchronous processing, the image processing will be done
     * on a seperate thread (AsyncTask)
     */
    public static final int ASYNC = 2;

    /**
     * A mixture of synchronous and asynchronous. Which means, that
     * there are calculations that are done in a seperate thread while these
     * are push to the main thread.
     */
    public static final int HYBRID = 3;

    /**
     * The current bitmap the view is displaying.
     */
    @Nullable
    protected Bitmap mCurrentBitmap;

    /**
     * The type of processing this indicator is running on.
     */
    @IndicationProcessingType
    private int mIndicationProcess;

    /**
     * The bitmap when onPreProgress is called
     */
    protected Bitmap mPreBitmap;

    /**
     * Standard constructor. Initializes a ProgressIndicator instance.
     *
     * @param indicationProcess the type of processing this indicator should have.
     */
    public ProgressIndicator(@IndicationProcessingType int indicationProcess) {
        mIndicationProcess = indicationProcess;
    }

    /**
     * Called once at the beginning before the action progress is called. This method
     * allows for instance to do some Bitmap manipulation before the progress starts.
     *
     * @param originalBitmap the
     */
    public void onPreProgress(Bitmap originalBitmap) {
        throw new UnsupportedOperationException("onPreProgress is not implemented");
    }


    /**
     * Called when the progress bar is moving.
     *
     * @param originalBitmap  the original bitmap
     * @param progressPercent the values in percent. Goes from 0 to 100
     */
    public synchronized void onProgress(Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        throw new UnsupportedOperationException("onProgress is not implemented");
    }

    /**
     * The current displayed bitmap.
     *
     * @return the current bitmap.
     */
    @Nullable
    public Bitmap getCurrentBitmap() {
        return mCurrentBitmap;
    }

    /**
     * Should be called when the indication is done.
     */
    @CallSuper
    public void cleanUp() {
        mCurrentBitmap = null;
    }

    /**
     * @return The indicator processing type this indicator is running on.
     * @see IndicationProcessingType
     */
    @IndicationProcessingType
    public int getIndicationProcessingType() {
        return mIndicationProcess;
    }

    /**
     * Callback interface when
     */
    public interface OnProgressIndicationUpdatedListener {
        void onProgressIndicationUpdated(final Bitmap bitmap);
    }
}
