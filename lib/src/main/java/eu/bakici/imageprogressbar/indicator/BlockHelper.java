package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for calculating the image slicing in blocks.
 */
class BlockHelper {

    /**
     * The blocks in rect objects.
     */
    private List<Rect> mBlocks;
    /**
     * The number of blocks in this bitmap.
     */
    private int mBlockSum;
    /**
     * Size of one block.
     */
    private int mPixels;

    /**
     *
     * @param size size in pixels or predefined {@link BlockIndicator.BlockSize}
     */
    public BlockHelper(Bitmap originalBitmap, final int size) {
        mPixels = size;
        initBlocks(originalBitmap);
    }

    private void initBlocks(Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        // adjusting the number of rows and columns
        int numberOfCols = (width / mPixels) + 1;
        int numberOfRows = (height / mPixels) + 1;
        int blockSumSize = mPixels;

        mBlockSum = numberOfCols * numberOfRows;
        mBlocks = new ArrayList<>(mBlockSum);

        for (int i = 0; i < mBlockSum; i++) {
            int col = i % numberOfCols;
            int row = i / numberOfCols;

            int left = col * blockSumSize;
            int top = row * blockSumSize;
            int right = Math.min(left + blockSumSize, width);
            int bottom = Math.min(top + blockSumSize, height);
            mBlocks.add(new Rect(left, top, right, bottom));
        }
    }

    public List<Rect> getBlocks() {
        return mBlocks;
    }

    public int getBlockSum() {
        return mBlockSum;
    }

    public Rect getRectFromPosition(Point point) {
        return getRectFromPosition(point.x, point.y);
    }

    public Rect getRectFromPosition(int x, int y) {
        for (Rect block : mBlocks) {
            if (block.contains(x, y)) {
                return block;
            }
        }
        return null;
    }
}
