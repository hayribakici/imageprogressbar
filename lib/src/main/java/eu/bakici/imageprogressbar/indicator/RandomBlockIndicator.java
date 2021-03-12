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
import android.support.annotation.NonNull;

import java.util.Collections;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;


/**
 * Indicator that fills the image by randomly placing colored blocks of the image.
 */
public class RandomBlockIndicator extends BlockIndicator {

    private int currProgressPercent = 0;

    private int currBlockPosOfPercent = 0;


    public RandomBlockIndicator() {
        this(BLOCK_SIZE_MEDIUM);
    }

    public RandomBlockIndicator(final int pixels) {
        super(pixels);
    }

    @Override
    protected void onPostBlockInitialization() {
        Collections.shuffle(blocks);
    }

    public Bitmap getBitmapOnProgress(final @NonNull Bitmap originalBitmap, final int progressPercent, final OnProgressIndicationUpdatedListener callback) {

        final int height = this.height;
        final int width = this.width;
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        int blockPosOfPercent = IndicatorUtils.getValueOfPercent(blockSum, progressPercent) + 1;

        if (blockPosOfPercent - currBlockPosOfPercent > 1) {
            // we need to cover all block positions
            // when blockSum is big, we might skip some positions,
            // therefore we are catching up.
            int diffPercent = blockPosOfPercent - currBlockPosOfPercent;
            blockUpdatedHandler.post(new CatchUpBlocksRunnable(diffPercent, originalBitmap, output, canvas, currBlockPosOfPercent, callback));
            currBlockPosOfPercent = blockPosOfPercent;
            return null;
        }

        currBlockPosOfPercent = blockPosOfPercent;

        if (currProgressPercent < progressPercent - 1) {
            // we have a rather large progressbar jump
            final int diffPercent = progressPercent - currProgressPercent;
            uIHandler.post(new ProgressJumpRunnable(diffPercent, originalBitmap, output, canvas, currProgressPercent, callback));
            currProgressPercent = progressPercent;
            return null;
        }
        currProgressPercent = progressPercent;

        addColorBlockToBitmap(originalBitmap, canvas, blockPosOfPercent - 1);
        preProgressBitmap.recycle();
        preProgressBitmap = output;
        callback.onProgressIndicationUpdated(output);
        return null;
    }

    private void addColorBlockToBitmap(final Bitmap originalBitmap, final Canvas canvas, final int blockPos) {
        if (blockPos >= blocks.size()) {
            return;
        }
        final Rect randomBlock = blocks.get(blockPos);
        final Paint paint = new Paint();
        canvas.drawBitmap(preProgressBitmap, 0, 0, paint);
        canvas.drawBitmap(originalBitmap, randomBlock, randomBlock, paint);
    }

    private class ProgressJumpRunnable implements Runnable {

        private final int mDiff;

        private final Canvas mCanvas;

        private final Bitmap mBitmap;

        private final Bitmap mOutput;

        private final int mCurr;

        private final OnProgressIndicationUpdatedListener mListener;

        ProgressJumpRunnable(int diff, Bitmap source, Bitmap output, Canvas canvas, int curr, OnProgressIndicationUpdatedListener listener) {
            mDiff = diff;
            mBitmap = source;
            mOutput = output;
            mCanvas = canvas;
            mCurr = curr;
            mListener = listener;
        }

        @Override
        public void run() {
            synchronized (RandomBlockIndicator.this) {
                for (int i = 1; i <= mDiff; i++) {
                    final int missingProgressPercent = mCurr + i;
                    int percent = IndicatorUtils.getValueOfPercent(blockSum, missingProgressPercent);
                    addColorBlockToBitmap(mBitmap, mCanvas, percent - 1);
                    preProgressBitmap = mOutput;
                    uIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onProgressIndicationUpdated(mOutput);
                        }
                    });

                }
            }
        }
    }


    private class CatchUpBlocksRunnable implements Runnable {

        private final int mDiff;

        private final Canvas mCanvas;

        private final Bitmap mBitmap;

        private final Bitmap mOutput;

        private final int mCurr;

        private final OnProgressIndicationUpdatedListener mListener;

        CatchUpBlocksRunnable(int diff, Bitmap source, Bitmap output, Canvas canvas, int curr, OnProgressIndicationUpdatedListener listener) {
            mDiff = diff;
            mBitmap = source;
            mOutput = output;
            mCanvas = canvas;
            mCurr = curr;
            mListener = listener;
        }

        @Override
        public void run() {
            synchronized (RandomBlockIndicator.this) {
                for (int i = 1; i <= mDiff; i++) {
                    final int missingProgressPercent = mCurr + i;
                    addColorBlockToBitmap(mBitmap, mCanvas, missingProgressPercent - 1);

                    preProgressBitmap = mOutput;
                    uIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onProgressIndicationUpdated(mOutput);
                        }
                    });

                }
            }
        }
    }
}
