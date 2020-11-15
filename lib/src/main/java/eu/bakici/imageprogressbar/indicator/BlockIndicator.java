package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public abstract class BlockIndicator extends HybridIndicator {

    @IntDef(value = {
            BLOCK_SIZE_BIG,
            BLOCK_SIZE_MEDIUM,
            BLOCK_SIZE_SMALL,
            BLOCK_SIZE_EXTRA_SMALL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BlockSize {}

    public static final int BLOCK_SIZE_BIG = 60;
    public static final int BLOCK_SIZE_MEDIUM = 50;
    public static final int BLOCK_SIZE_SMALL = 30;
    public static final int BLOCK_SIZE_EXTRA_SMALL = 20;

    protected BlockHelper mBlockHelper;

    /**
     * Size of one block.
     */
    protected int mPixels;


    /**
     * Standard constructor with {@link #BLOCK_SIZE_MEDIUM}
     */
    public BlockIndicator() {
        this(BLOCK_SIZE_MEDIUM);
    }

    /**
     *
     * @param size size in pixels or predefined {@link BlockSize}
     */
    public BlockIndicator(final int size) {
        mPixels = size;
    }

    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        mBlockHelper = new BlockHelper(originalBitmap, mPixels);
        mPreBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        onPostBlockInitialization();
        mCurrentBitmap = mPreBitmap;
    }

    protected void onPostBlockInitialization() {
        // in case someone wants to do something after the blocks have been initialized.
    }
}
