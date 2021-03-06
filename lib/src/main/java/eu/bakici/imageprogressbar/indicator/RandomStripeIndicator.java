package eu.bakici.imageprogressbar.indicator;

/*
 * Copyright (C) 2021 Hayri Bakici
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
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.bakici.imageprogressbar.utils.FlaggedRect;
import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class RandomStripeIndicator extends HybridIndicator {

    public final static int LEVEL_THIN = 4;
    public final static int LEVEL_MEDIUM = 8;
    public final static int LEVEL_THICK = 16;
    @StripeThickness
    private final int thickness;
    private final List<FlaggedRect> stripes;
    private int currProgressPercent = 0;
    private int currBlockPosOfPercent = 0;

    public RandomStripeIndicator(@StripeThickness int thickness) {
        this.thickness = thickness;
        stripes = new ArrayList<>();
    }

    @Override
    public void onPreProgress(Bitmap originalBitmap) {
        preBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        currentBitmap = preBitmap;

        int width = originalBitmap.getWidth();
        for (int i = 0; i < width + thickness; i += thickness) {
            stripes.add(new FlaggedRect(Math.min(i, width), 0, Math.min(i + thickness, width), originalBitmap.getHeight()));
        }
        Collections.shuffle(stripes);
    }

    @Override
    public void onProgress(Bitmap originalBitmap, int progressPercent, final OnProgressIndicationUpdatedListener callback) {
        if (progressPercent == 0) {
            return;
        }
        int percent = IndicatorUtils.calcPercent(stripes.size(), progressPercent);

        final Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        if (percent - currBlockPosOfPercent > 1) {
            // we need to cover all block positions
            // when blockSum is big, we might skip some positions,
            // therefore we are catching up.
            int diffPercent = percent - currBlockPosOfPercent;
            blockUpdatedHandler.post(new CatchUpStripesRunnable(diffPercent, originalBitmap, output, canvas, currBlockPosOfPercent, callback));
            currBlockPosOfPercent = percent;
            return;
        }

        currBlockPosOfPercent = percent;

        if (currProgressPercent < progressPercent - 1) {
            // we have a rather large progressbar jump
            final int diffPercent = progressPercent - currProgressPercent;
            uIHandler.post(new ProgressJumpRunnable(diffPercent, originalBitmap, output, canvas, currProgressPercent, callback));
            currProgressPercent = progressPercent;
            return;
        }
        currProgressPercent = progressPercent;

        addColorStripeToBitmap(originalBitmap, canvas, percent - 1);
        preBitmap.recycle();
        preBitmap = output;
        callback.onProgressIndicationUpdated(output);
    }

    private void addColorStripeToBitmap(final Bitmap originalBitmap, final Canvas canvas, final int pos) {
        if (pos >= stripes.size()) {
            // insanity check
            return;
        }
        final FlaggedRect randomStripe = stripes.get(pos);
        final Paint paint = new Paint();
        canvas.drawBitmap(preBitmap, 0, 0, paint);
        if (!randomStripe.isFlagged()) {
            canvas.drawBitmap(originalBitmap, randomStripe.getRect(), randomStripe.getRect(), paint);
            randomStripe.setFlagged(true);
        }

    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        if (handlerThread.isAlive()) {
            handlerThread.quit();
        }
    }

    /**
     * Type of lines the image will be filled.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            LEVEL_THIN,
            LEVEL_MEDIUM,
            LEVEL_THICK
    })
    public @interface StripeThickness {
    }

    private class ProgressJumpRunnable implements Runnable {

        private final int diff;

        private final Canvas canvas;

        private final Bitmap bitmap;

        private final Bitmap output;

        private final int curr;

        private final OnProgressIndicationUpdatedListener listener;

        ProgressJumpRunnable(int diff, Bitmap source, Bitmap output, Canvas canvas, int curr, OnProgressIndicationUpdatedListener listener) {
            this.diff = diff;
            bitmap = source;
            this.output = output;
            this.canvas = canvas;
            this.curr = curr;
            this.listener = listener;
        }

        @Override
        public void run() {
            synchronized (RandomStripeIndicator.this) {
                for (int i = 1; i <= diff; i++) {
                    final int missingProgressPercent = curr + i;
                    int percent = IndicatorUtils.calcPercent(stripes.size(), missingProgressPercent);
                    addColorStripeToBitmap(bitmap, canvas, percent - 1);
                    preBitmap = output;
                    uIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgressIndicationUpdated(output);
                        }
                    });

                }
            }
        }
    }


    private class CatchUpStripesRunnable implements Runnable {

        private final int diff;

        private final Canvas canvas;

        private final Bitmap bitmap;

        private final Bitmap output;

        private final int curr;

        private final OnProgressIndicationUpdatedListener listener;

        CatchUpStripesRunnable(int diff, Bitmap source, Bitmap output, Canvas canvas, int curr, OnProgressIndicationUpdatedListener listener) {
            this.diff = diff;
            bitmap = source;
            this.output = output;
            this.canvas = canvas;
            this.curr = curr;
            this.listener = listener;
        }

        @Override
        public void run() {
            synchronized (RandomStripeIndicator.this) {
                for (int i = 1; i <= diff; i++) {
                    final int missingProgressPercent = curr + i;
                    addColorStripeToBitmap(bitmap, canvas, missingProgressPercent - 1);

                    preBitmap = output;
                    uIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgressIndicationUpdated(output);
                        }
                    });

                }
            }
        }
    }
}
