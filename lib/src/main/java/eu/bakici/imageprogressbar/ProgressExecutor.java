package eu.bakici.imageprogressbar;

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
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.bakici.imageprogressbar.indicator.Indicator;
import eu.bakici.imageprogressbar.utils.IndicatorUtils;

import static eu.bakici.imageprogressbar.utils.IndicatorUtils.integerizePercent;

/**
 * Helper class.
 */
final class ProgressExecutor {


    @NonNull
    private final OnPostExecuteListener<Bitmap> listener;
    @NonNull
    private final ExecutorService executor;
    @NonNull
    private final Handler mainThreadHandler;
    @NonNull
    private final Bitmap originalBitmap;
    @NonNull
    Indicator indicator;

    public ProgressExecutor(@NonNull Bitmap originalBitmap,
                            @NonNull Indicator indicator,
                            @NonNull OnPostExecuteListener<Bitmap> listener) {
        this.originalBitmap = originalBitmap;
        this.indicator = indicator;
        this.listener = listener;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    void prepare() {
        executor.execute(() -> {
            indicator.onPreProgress(originalBitmap);
            callback();
        });
    }

    void start(@FloatRange(from = 0.0, to = 1.0) float progress) {
        executor.execute(() -> {
//            if (indicator instanceof CatchUpIndicator) {
//                catchUp(progress);
//            } else {
            indicator.onProgress(originalBitmap, progress);
//            }
            // post back to main thread
            callback();
        });
    }

    private void catchUp(@FloatRange(from = 0.0, to = 1.0) float progress) {
        int currProgress = integerizePercent(indicator.getCurrentProgressPercent());
        int p = integerizePercent(progress);
        if (currProgress < p - 1) {
            // large progressbar jump
            final int diff = p - currProgress;
            for (int i = 1; i <= diff; i++) {
                final int missingProgressPercent = currProgress + i;
                indicator.onProgress(originalBitmap, IndicatorUtils.floatPercent(missingProgressPercent));
                callback();
            }
        }
    }

    private void callback() {
        mainThreadHandler.post(() -> listener.onPostExecute(indicator.getCurrentBitmap()));
    }

    interface OnPostExecuteListener<T> {
        void onPostExecute(T param);
    }
}
