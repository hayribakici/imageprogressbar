package eu.bakici.imageprogressbar.indicator;

/*
 * Copyright (C) 2021 hayribakici
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
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.bakici.imageprogressbar.utils.FlaggedRect;
import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class RandomStripeIndicator extends CatchUpIndicator {

    public final static int LEVEL_THIN = 4;
    public final static int LEVEL_MEDIUM = 8;
    public final static int LEVEL_THICK = 16;
    @StripeThickness
    private final int thickness;
    private final List<FlaggedRect> stripes;
    private int currProgressPercent = 0;
    private int currPercent = 0;
    private int prevProgressPercent;

    public RandomStripeIndicator(@StripeThickness int thickness) {
        this.thickness = thickness;
        stripes = new ArrayList<>();
    }

    @Override
    public Bitmap getPreProgressBitmap(@NonNull Bitmap originalBitmap) {

        int width = originalBitmap.getWidth();
        for (int i = 0; i < width + thickness + 1; i += thickness) {
            stripes.add(new FlaggedRect(Math.min(i, width), 0, Math.min(i + thickness, width), originalBitmap.getHeight()));
        }
        Collections.shuffle(stripes);
        return IndicatorUtils.convertGrayscale(originalBitmap);
    }


    public Bitmap getBitmapOnProgress(@NonNull Bitmap originalBitmap, int progressPercent, final OnProgressIndicationUpdatedListener callback) {

        int stripeCount = stripes.size();

        int value = IndicatorUtils.getValueOfPercent(stripeCount, progressPercent);

        final Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        Log.d("Stripe", String.format("progressPercent %s, previousProgressPercent %s", progressPercent, prevProgressPercent));
        if (progressPercent - prevProgressPercent > 1) {
            // we need to cover all block positions
            // when stripeCount is large, we might skip some positions,
            // when seeking through the SeekBar. Therefore we are catching up

            int prevValue = IndicatorUtils.getValueOfPercent(stripeCount, prevProgressPercent);
//            Log.d("Stripe", "diffPercent " + IndicatorUtils.getValueOfPercent(stripeCount, diffPercent));
//            blockUpdatedHandler.post(new CatchUpStripesRunnable(originalBitmap, output, canvas, value, prevValue, callback));

            prevProgressPercent = progressPercent;
            return output;
        }
        prevProgressPercent = progressPercent;
        currPercent = value;

//        if (currProgressPercent < progressPercent - 1) {
//            // we have a rather large progressbar jump
//            final int diffPercent = progressPercent - currProgressPercent;
//            uIHandler.post(new ProgressJumpRunnable(diffPercent, originalBitmap, output, canvas, currProgressPercent, callback));
//            currProgressPercent = progressPercent;
//            return;
//        }
        currProgressPercent = progressPercent;

//        addColorStripeToBitmap(originalBitmap, canvas, value - 1);
////        preProgressBitmap.recycle();
//        preProgressBitmap = output;
//        callback.onProgressIndicationUpdated(output);
        return null;
    }


//    private void addColorStripeToBitmap(final Bitmap originalBitmap, final Canvas canvas, final int pos) {
//        if (pos >= stripes.size()) {
//            // insanity check
//            return;
//        }
//        for (int i = Math.max(0, pos - 2); i <= pos + 2; i++) {
//            final FlaggedRect randomStripe = stripes.get(pos);
//            final Paint paint = new Paint();
//            canvas.drawBitmap(preProgressBitmap, 0, 0, paint);
//            if (!randomStripe.isFlagged()) {
//                canvas.drawBitmap(originalBitmap, randomStripe.getRect(), randomStripe.getRect(), paint);
//                randomStripe.setFlagged(true);
//            }
//        }
//
//    }


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
            this.bitmap = source;
            this.output = output;
            this.canvas = canvas;
            this.curr = curr;
            this.listener = listener;
        }

        @Override
        public void run() {
            synchronized (RandomStripeIndicator.this) {
//                for (int i = 1; i <= diff; i++) {
//                    final int missingProgressPercent = curr + i;
//                    int percent = IndicatorUtils.getValueOfPercent(stripes.size(), missingProgressPercent);
//                    addColorStripeToBitmap(bitmap, canvas, percent - 1);
//                    preProgressBitmap = output;
//                    uIHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            listener.onProgressIndicationUpdated(output);
//                        }
//                    });

            }
        }
    }
}


//    private class CatchUpStripesRunnable implements Runnable {
//
//        private final int prevValue;
//        private final Canvas canvas;
//        private final Bitmap bitmap;
//        private final Bitmap output;
//        private final int currValue;
//        private final OnProgressIndicationUpdatedListener listener;
//
//        CatchUpStripesRunnable(Bitmap source, Bitmap output, Canvas canvas, int currValue, int prevValue, OnProgressIndicationUpdatedListener listener) {
//            this.bitmap = source;
//            this.output = output;
//            this.canvas = canvas;
//            this.currValue = currValue;
//            this.prevValue = prevValue;
//            this.listener = listener;
//        }
//
//        @Override
//        public void run() {
//            synchronized (RandomStripeIndicator.this) {
//                for (int i = prevValue; i <= currValue; i++) {
//                    addColorStripeToBitmap(bitmap, canvas, i);
//
//                    preProgressBitmap = output;
////                    uIHandler.post(() -> listener.onProgressIndicationUpdated(output));
//
//                }
//            }
//        }
//    }
//}
