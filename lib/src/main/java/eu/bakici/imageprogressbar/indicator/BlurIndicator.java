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

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class BlurIndicator extends ProgressIndicator {

    private static final String TAG = BlurIndicator.class.getSimpleName();

    private static final int MAX_RADIUS = 25;

    private final Context context;

    public BlurIndicator(final Context context) {
        super(ASYNC);
        this.context = context;
    }

    @Override
    public Bitmap getPreProgressBitmap(final Bitmap originalBitmap) {
        return Blur.fastblur(context, originalBitmap, MAX_RADIUS);
    }

    @Override
    public synchronized Bitmap getBitmapOnProgress(final @NonNull Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {

        if (progressPercent == 100) {
            return originalBitmap;
        }
        final int radius = MAX_RADIUS - IndicatorUtils.getValueOfPercent(MAX_RADIUS, progressPercent);
        if (radius <= 0) {
            // insanity check
            return originalBitmap;
        }
        Log.d(TAG, "snapshot = " + radius);
        return Blur.fastblur(context, originalBitmap, radius);
    }

}
