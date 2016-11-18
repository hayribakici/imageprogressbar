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
import android.util.Log;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class BlurIndicator extends ProgressIndicator {

    private static final String TAG = BlurIndicator.class.getSimpleName();

    private static final int MAX_RADIUS = 25;

    private final Context mContext;

    public BlurIndicator(final Context context) {
        super(ASYNC);
        mContext = context;
    }

    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        mCurrentBitmap = Blur.fastblur(mContext, originalBitmap, MAX_RADIUS);
    }

    @Override
    public synchronized void onProgress(final Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {

        if (progressPercent == 100) {
            mCurrentBitmap = originalBitmap;
        }
        final int radius = MAX_RADIUS - IndicatorUtils.calcPercent(MAX_RADIUS, progressPercent);
        if (radius <= 0) {
            // insanity check
            mCurrentBitmap = originalBitmap;
            return;
        }
        Log.d(TAG, "snapshot = " + radius);
        mCurrentBitmap = Blur.fastblur(mContext, originalBitmap, radius);
    }

}
