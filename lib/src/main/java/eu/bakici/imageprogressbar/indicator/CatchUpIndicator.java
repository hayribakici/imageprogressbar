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
import android.os.Handler;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

import static eu.bakici.imageprogressbar.utils.IndicatorUtils.integerizePercent;

/**
 * An indicator that is a synchronous indicator at its core, but does now and then gives asynchronous
 * callbacks. This is good, if the progress percents becomes jumpy, meaning there is not linear
 * increase of the progress. This indicator is there to 'fill the gaps' and let progression
 * catch on from where the progress jump started until its desired progression.
 */
public abstract class CatchUpIndicator extends Indicator {

    protected Handler uIHandler;


    protected Handler blockUpdatedHandler;

//    private Comparable<? extends Number>

    public CatchUpIndicator() {
        super();

    }

    @Override
    public Bitmap getBitmap(final @NonNull Bitmap originalBitmap, @FloatRange(from = 0.0, to = 1.0) float progress) {
        return null;
    }

    @Override
    public void onProgress(@NonNull Bitmap originalBitmap, float progressPercent) {
        int currProgress = integerizePercent(currentProgressPercent);
        int p = integerizePercent(progressPercent);
        if (currProgress < p - 1) {
            // large progressbar jump
            final int diff = p - currProgress;
            Log.d("Ketchup", "diff: " + diff);
            for (int i = 1; i <= diff; i++) {
                final int missingProgressPercent = currProgress + i;
                currentBitmap = getBitmap(originalBitmap, IndicatorUtils.floatPercent(missingProgressPercent));
            }
        }
        currentProgressPercent = progressPercent;
    }

    /**
     * Callback interface when the indication has been updated.
     */
    public interface OnProgressIndicationUpdatedListener {
        void onProgressIndicationUpdated(final Bitmap bitmap);
    }
}
