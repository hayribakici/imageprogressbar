package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

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

    protected Bitmap mPreBitmap;

    /**
     * The blocks in rect objects.
     */
    protected List<Rect> mBlocks;
    /**
     * The number of blocks in this bitmap.
     */
    protected int mBlockSum;
    /**
     * The width of the bitmap.
     */
    protected int mWidth;
    /**
     * The height of the bitmap.
     */
    protected int mHeight;
    /**
     * Size of one block.
     */
    protected int mPixels;


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
        mPreBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        mWidth = originalBitmap.getWidth();
        mHeight = originalBitmap.getHeight();
        // adjusting the number of rows and columns
        int numberOfCols = (mWidth / mPixels) + 1;
        int numberOfRows = (mHeight / mPixels) + 1;
        int blockSumSize = mPixels;

        mBlockSum = numberOfCols * numberOfRows;
        mBlocks = new ArrayList<>(mBlockSum);

        for (int i = 0; i < mBlockSum; i++) {
            int col = i % numberOfCols;
            int row = i / numberOfCols;

            int left = col * blockSumSize;
            int top = row * blockSumSize;
            int right = Math.min(left + blockSumSize, mWidth);
            int bottom = Math.min(top + blockSumSize, mHeight);
            Rect block = new Rect(left, top, right, bottom);
            mBlocks.add(block);
        }
        onPostBlockInitialization();
        mCurrentBitmap = mPreBitmap;
    }

    protected void onPostBlockInitialization() {
        // in case someone wants to do something after the blocks have been initialized.
    }
}
