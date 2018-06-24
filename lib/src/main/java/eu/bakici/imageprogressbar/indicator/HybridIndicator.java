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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.IntRange;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

/**
 * An indicator that is a synchronous indicator at its core, but does now and then gives asynchronous
 * callbacks. This is good, if the progress percents becomes jumpy, meaning there is not linear
 * increase of the progress. This indicator is there to 'fill the gaps' and let progression
 * catch on from where the progress jump started until its desired progression.
 */
public class HybridIndicator extends ProgressIndicator {

    private int currProgressPercent = 0;
    private int currPercent = 0;
    private Handler uIHandler;
    private HandlerThread handlerThread;
    private Handler blockUpdatedHandler;

    public HybridIndicator() {
        super(HYBRID);
        uIHandler = new Handler(Looper.getMainLooper());
        handlerThread = new HandlerThread("jumper", HandlerThread.MIN_PRIORITY);
        handlerThread.start();
        blockUpdatedHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public final void onProgress(final Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        throw new UnsupportedOperationException("onProgress is not implemented");
        // onProgress(Bitmap, progressPercent, listener) is used
    }


    /**
     * Same as {@link #onProgress(Bitmap, int)} but with a callback.
     *
     * @param originalBitmap  the original bitmap.
     * @param progress the percentage of the current progress.
     * @param listener        a callback listener for filling the gaps between progress jumps.
     */
    public void onProgress(final Bitmap originalBitmap,
                           @IntRange(from = 0, to = 100) int progress,
                           final OnProgressIndicationUpdatedListener listener) {
        final int height = originalBitmap.getHeight();
        final int width = originalBitmap.getWidth();
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        int percent = IndicatorUtils.calcPercent(maxValue, progress) + 1;

        if (percent - currPercent > 1) {
            // we need to cover all block positions
            // when blockSum is big, we might skip some positions,
            // therefore we are catching up.
            int diffPercent = percent - currPercent;
            blockUpdatedHandler.post(new CatchUpRunnable(diffPercent, originalBitmap, output, canvas, currPercent, listener));
            currPercent = percent;
            return;
        }

        currPercent = percent;

        if (currProgressPercent < progress - 1) {
            // we have a rather large progressbar jump
            final int diffPercent = progress - currProgressPercent;
            uIHandler.post(new ProgressJumpRunnable(diffPercent, originalBitmap, output, canvas, currProgressPercent, listener));
            currProgressPercent = progress;
            return;
        }
        currProgressPercent = progress;

        fillBitmap(originalBitmap, canvas, percent - 1);
        preBitmap.recycle();
        preBitmap = output;
        listener.onProgressIndicationUpdated(output);
    }

    protected void fillBitmap(final Bitmap originalBitmap, final Canvas canvas, final int blockPos) {

    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        if (handlerThread.isAlive()) {
            handlerThread.quit();
        }
    }

    private class ProgressJumpRunnable implements Runnable {

        private final int diff;
        private final Canvas canvas;
        private final Bitmap bitmap;
        private final Bitmap output;
        private final int current;
        private final OnProgressIndicationUpdatedListener listener;

        ProgressJumpRunnable(int diff,
                             Bitmap source,
                             Bitmap output,
                             Canvas canvas,
                             int current,
                             OnProgressIndicationUpdatedListener listener) {
            this.diff = diff;
            this.bitmap = source;
            this.output = output;
            this.canvas = canvas;
            this.current = current;
            this.listener = listener;
        }

        @Override
        public void run() {
            synchronized (HybridIndicator.this) {
                for (int i = 1; i <= diff; i++) {
                    final int missingProgressPercent = current + i;
                    int percent = IndicatorUtils.calcPercent(maxValue, missingProgressPercent);
                    fillBitmap(bitmap, canvas, percent - 1);
                    preBitmap = output;
                    uIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgressIndicationUpdated(output);
                        }
                    });
                }
            }
            /*
            ynchronized (HybridIndicator.this) {
                for (int i = 1; i <= diff; i++) {
                    final int missingProgressPercent = current + i;
                    fillBitmap(source, canvas, missingProgressPercent - 1);

                    preBitmap = output;
                    uIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onProgressIndicationUpdated(output);
                        }
                    });

                }
             */
        }
    }


    private class CatchUpRunnable implements Runnable {

        private final int diff;
        private final Canvas canvas;
        private final Bitmap source;
        private final Bitmap output;
        private final int current;

        private final OnProgressIndicationUpdatedListener mListener;

        CatchUpRunnable(int diff,
                        Bitmap source,
                        Bitmap output,
                        Canvas canvas,
                        int curr,
                        OnProgressIndicationUpdatedListener listener) {
            this.diff = diff;
            this.source = source;
            this.output = output;
            this.canvas = canvas;
            current = curr;
            mListener = listener;
        }

        @Override
        public void run() {
            synchronized (HybridIndicator.this) {
                for (int i = 1; i <= diff; i++) {
                    final int missingProgressPercent = current + i;
                    fillBitmap(source, canvas, missingProgressPercent - 1);

                    preBitmap = output;
                    uIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onProgressIndicationUpdated(output);
                        }
                    });

                }
            }
        }
    }

    /**
     * Callback interface when the indication has been updated.
     */
    public interface OnProgressIndicationUpdatedListener {
        void onProgressIndicationUpdated(final Bitmap bitmap);
    }
}
