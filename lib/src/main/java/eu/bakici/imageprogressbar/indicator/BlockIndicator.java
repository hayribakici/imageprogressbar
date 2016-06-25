package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public abstract class BlockIndicator extends HybridIndicator {


    protected static final int BLOCK_SUM_BIG = 60;
    protected static final int BLOCK_SUM_MEDIUM = 50;
    protected static final int BLOCK_SUM_SMALL = 30;
    protected static final int BLOCK_SUM_EXTRA_SMALL = 20;

    protected Bitmap mPreBitmap;

    protected List<Rect> mBlocks;
    protected int mBlockSum;
    protected int mWidth;
    protected int mHeight;
    protected int mPixels;


    public BlockIndicator() {
        this(BlockSize.MEDIUM);
    }

    public BlockIndicator(final BlockSize size) {
        this(toPixels(size));
    }

    public BlockIndicator(final int pixels) {
        super();
        mPixels = pixels;
    }

    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        mPreBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        mWidth = originalBitmap.getWidth();
        mHeight = originalBitmap.getHeight();
        // adjusting the number of rows and columns
        final int numberOfCols = (mWidth / mPixels) + 1;
        final int numberOfRows = (mHeight / mPixels) + 1;
        final int blockSumSize = mPixels;

        mBlockSum = numberOfCols * numberOfRows;
        mBlocks = new ArrayList<>(mBlockSum);

        for (int i = 0; i < mBlockSum; i++) {
            final int col = i % numberOfCols;
            final int row = i / numberOfCols;

            final int left = col * blockSumSize;
            final int top = row * blockSumSize;
            final int right = Math.min(left + blockSumSize, mWidth);
            final int bottom = Math.min(top + blockSumSize, mHeight);
            final Rect block = new Rect(left, top, right, bottom);
            mBlocks.add(block);
        }
        onPostBlockInitialization();
        mCurrentBitmap = mPreBitmap;
    }

    protected void onPostBlockInitialization() {
        // in case someone wants to do something after the blocks have been initialized.
    }

    protected static int toPixels(final BlockSize blockSize) {
        switch (blockSize) {
            case BIG: return BLOCK_SUM_BIG;
            case SMALL: return BLOCK_SUM_SMALL;
            case EXTRA_SMALL: return BLOCK_SUM_EXTRA_SMALL;
            case MEDIUM:
            default: return BLOCK_SUM_MEDIUM;
        }
    }

    public enum BlockSize {
        BIG,
        MEDIUM,
        SMALL,
        EXTRA_SMALL
    }

}
