package eu.bakici.imageprogressbar.indicator;

/*
 * Copyright (C) 2016 Hayri Bakici
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

/**
 * Helper class that slices the image into blocks.
 */
public abstract class BlockIndicator extends HybridIndicator {



    @IntDef(value = {
            BLOCK_SIZE_BIG,
            BLOCK_SIZE_MEDIUM,
            BLOCK_SIZE_SMALL,
            BLOCK_SIZE_EXTRA_SMALL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BlockSize {}

    public static final int BLOCK_SIZE_BIG = 75;
    public static final int BLOCK_SIZE_MEDIUM = 50;
    public static final int BLOCK_SIZE_SMALL = 30;
    public static final int BLOCK_SIZE_EXTRA_SMALL = 15;

    /**
     * The blocks in rect objects.
     */
    protected List<Rect> blocks;
    /**
     * The number of blocks in this bitmap.
     */
    protected int blockSum;
    /**
     * The width of the bitmap.
     */
    protected int width;
    /**
     * The height of the bitmap.
     */
    protected int height;
    /**
     * Size of one block.
     */
    protected int pixels;

    /**
     * Number of columns the bitmap is sliced.
     */
    protected int numberOfCols;

    /**
     * Number of rows the bitmap is sliced.
     */
    protected int numberOfRows;


    public BlockIndicator() {
        this(BLOCK_SIZE_MEDIUM);
    }

    /**
     *
     * @param size size in pixels or predefined {@link BlockSize}
     */
    public BlockIndicator(final int size) {
        pixels = size;
    }

    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        preBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        width = originalBitmap.getWidth();
        height = originalBitmap.getHeight();
        // adjusting the number of rows and columns
        numberOfCols = (width / pixels) + 1;
        numberOfRows = (height / pixels) + 1;
        int blockSize = pixels;

        blockSum = numberOfCols * numberOfRows;
        blocks = new ArrayList<>(blockSum);

        int pivotIndex;
        for (int i = 0; i < blockSum; i++) {
            int col = i % numberOfCols;
            int row = i / numberOfCols;
            int mod = row % 2;

            int left = col * blockSize;
            int top = row * blockSize;
            int right = Math.min(left + blockSize, width);
            int bottom = Math.min(top + blockSize, height);
            Rect block = new Rect(left, top, right, bottom);
            int index;
            if (mod == 0) {
                index = i;
            } else {
                pivotIndex = i;
                index = i - pivotIndex + (numberOfCols - 1) - col;
            }


            blocks.add(index, block);
        }
        onPostBlockInitialization();
        currentBitmap = preBitmap;
        maxValue = blockSum;
    }

    protected void onPostBlockInitialization() {
        // in case someone wants to do something after the blocks have been initialized.
    }

    @Override
    protected void fillBitmap(final Bitmap originalBitmap, final Canvas canvas, final int blockPos) {
        if (blockPos >= blocks.size()) {
            return;
        }
        final Rect block = blocks.get(blockPos);
        final Paint paint = new Paint();
        canvas.drawBitmap(preBitmap, 0, 0, paint);
        canvas.drawBitmap(originalBitmap, block, block, paint);
    }
}
