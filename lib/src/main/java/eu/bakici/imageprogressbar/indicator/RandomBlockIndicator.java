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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.Collections;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;


/**
 * Indicator that fills the image by randomly placing colored blocks of the image.
 */
public class RandomBlockIndicator extends BlockIndicator {

    private int mCurrProgressPercent = 0;

    private int mCurrBlockPosOfPercent = 0;

    private Handler mUIHandler;

    private HandlerThread mHandlerThread;

    private Handler mBlockUpdatedHandler;


    public RandomBlockIndicator() {
        this(BLOCK_SIZE_MEDIUM);
    }

    public RandomBlockIndicator(final int pixels) {
        super(pixels);
        mUIHandler = new Handler(Looper.getMainLooper());
        mHandlerThread = new HandlerThread("jumper", HandlerThread.MIN_PRIORITY);
        mHandlerThread.start();
        mBlockUpdatedHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    protected void onPostBlockInitialization() {
        Collections.shuffle(mBlocks);
    }

    @Override
    public Bitmap createBitmapOnProgress(final Bitmap originalBitmap, final int progressPercent, final OnProgressIndicationUpdatedListener callback) {

        final int height = mHeight;
        final int width = mWidth;
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        int blockPosOfPercent = IndicatorUtils.calcPercent(mBlockSum, progressPercent) + 1;

        if (blockPosOfPercent - mCurrBlockPosOfPercent > 1) {
            // we need to cover all block positions
            // when mBlockSum is big, we might skip some positions,
            // therefore we are catching up.
            int diffPercent = blockPosOfPercent - mCurrBlockPosOfPercent;
            mBlockUpdatedHandler.post(new CatchUpBlocksRunnable(diffPercent, originalBitmap, output, canvas, mCurrBlockPosOfPercent, callback));
            mCurrBlockPosOfPercent = blockPosOfPercent;
            return null;
        }

        mCurrBlockPosOfPercent = blockPosOfPercent;

        if (mCurrProgressPercent < progressPercent - 1) {
            // we have a rather large progressbar jump
            final int diffPercent = progressPercent - mCurrProgressPercent;
            mUIHandler.post(new ProgressJumpRunnable(diffPercent, originalBitmap, output, canvas, mCurrProgressPercent, callback));
            mCurrProgressPercent = progressPercent;
            return null;
        }
        mCurrProgressPercent = progressPercent;

        addColorBlockToBitmap(originalBitmap, canvas, blockPosOfPercent - 1);
        mPreBitmap.recycle();
        mPreBitmap = output;
        callback.onProgressIndicationUpdated(output);
        return null;
    }

    private void addColorBlockToBitmap(final Bitmap originalBitmap, final Canvas canvas, final int blockPos) {
        if (blockPos >= mBlocks.size()) {
            return;
        }
        final Rect randomBlock = mBlocks.get(blockPos);
        final Paint paint = new Paint();
        canvas.drawBitmap(mPreBitmap, 0, 0, paint);
        canvas.drawBitmap(originalBitmap, randomBlock, randomBlock, paint);
    }
    @Override
    public void cleanUp() {
        super.cleanUp();
        if (mHandlerThread.isAlive()) {
            mHandlerThread.quit();
        }
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
                    int percent = IndicatorUtils.calcPercent(mBlockSum, missingProgressPercent);
                    addColorBlockToBitmap(mBitmap, mCanvas, percent - 1);
                    mPreBitmap = mOutput;
                    mUIHandler.post(new Runnable() {
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

                    mPreBitmap = mOutput;
                    mUIHandler.post(new Runnable() {
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
