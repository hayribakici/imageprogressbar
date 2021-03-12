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
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class AlphaIndicator extends ProgressIndicator {

    private static final int MAX_ALPHA = 255;

    private final Paint alphaPaint;

    public AlphaIndicator() {
        super(SYNC);
        alphaPaint = new Paint();
    }

    @Override
    public void onPreProgress(@NonNull Bitmap originalBitmap) {
        preProgressBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        currentBitmap = preProgressBitmap;
    }

    @Override
    public synchronized void onProgress(@NonNull Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        final Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        alphaPaint.setAlpha(IndicatorUtils.getValueOfPercent(MAX_ALPHA, progressPercent));

        canvas.drawBitmap(preProgressBitmap, 0, 0, new Paint());
        canvas.drawBitmap(originalBitmap, 0, 0, alphaPaint);
        currentBitmap = output;
    }
}
