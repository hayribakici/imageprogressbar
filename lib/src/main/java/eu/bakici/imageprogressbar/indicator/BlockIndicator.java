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
import android.graphics.Rect;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
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

    public static final int BLOCK_SIZE_BIG = 60;
    public static final int BLOCK_SIZE_MEDIUM = 50;
    public static final int BLOCK_SIZE_SMALL = 30;
    public static final int BLOCK_SIZE_EXTRA_SMALL = 20;

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
        int numberOfCols = (width / pixels) + 1;
        int numberOfRows = (height / pixels) + 1;
        int blockSumSize = pixels;

        blockSum = numberOfCols * numberOfRows;
        blocks = new ArrayList<>(blockSum);

        for (int i = 0; i < blockSum; i++) {
            int col = i % numberOfCols;
            int row = i / numberOfCols;

            int left = col * blockSumSize;
            int top = row * blockSumSize;
            int right = Math.min(left + blockSumSize, width);
            int bottom = Math.min(top + blockSumSize, height);
            Rect block = new Rect(left, top, right, bottom);
            blocks.add(block);
        }
        onPostBlockInitialization();
        currentBitmap = preBitmap;
    }

    protected void onPostBlockInitialization() {
        // in case someone wants to do something after the blocks have been initialized.
    }
}
