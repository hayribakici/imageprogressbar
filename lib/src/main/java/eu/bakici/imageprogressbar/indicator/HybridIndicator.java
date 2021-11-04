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
import android.os.HandlerThread;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * An indicator that is a synchronous indicator at its core, but does now and then gives asynchronous
 * callbacks. This is good, if the progress percents becomes jumpy, meaning there is not linear
 * increase of the progress. This indicator is there to 'fill the gaps' and let progression
 * catch on from where the progress jump started until its desired progression.
 */
public class HybridIndicator extends ProgressIndicator {

    protected Handler uIHandler;

    protected HandlerThread handlerThread;

    protected Handler blockUpdatedHandler;

    private int currProgressPercent;

    public HybridIndicator() {
        super(HYBRID);

    }

    @Override
    public final Bitmap getBitmap(final @NonNull Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        throw new UnsupportedOperationException("onProgress is not implemented");
        // onProgress(Bitmap, progressPercent, listener) is used
    }


    /**
     * Same as {@link ProgressIndicator#onProgress(Bitmap, int)} but with a callback.
     *
     * @param originalBitmap  the original bitmap.
     * @param progressPercent the percentage of the current progress.
     * @param listener        a callback listener for filling the gaps between progress jumps.
     */
    public final void onProgress(@NonNull final Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent, final OnProgressIndicationUpdatedListener listener) {
        currentBitmap = getBitmapOnProgress(originalBitmap, progressPercent, listener);
    }


    protected Bitmap getBitmapOnProgress(Bitmap originalBitmap, int progressPercent, OnProgressIndicationUpdatedListener listener) {
        return getCurrentBitmap();
    }

    // TODO add method that queues Runnables
    // TODO add method that posts to main thread
    // TODO add custom Runnable class
//    protected void queue() {
//
//    }


    /**
     * Callback interface when the indication has been updated.
     */
    public interface OnProgressIndicationUpdatedListener {
        void onProgressIndicationUpdated(final Bitmap bitmap);
    }
}
