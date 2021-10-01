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
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class PixelizeIndicator extends ProgressIndicator {

    private static final long TIME_BETWEEN_TASKS = 400;
    private static final float PROGRESS_TO_PIXELIZATION_FACTOR = 3000.f;


    private long lastTime;

    public PixelizeIndicator() {
        this(ASYNC);
    }

    public PixelizeIndicator(@IntRange(from = SYNC, to = ASYNC) @IndicationProcessingType int processingType) {
        super(processingType);
    }

    @Override
    public Bitmap getPreProgressBitmap(@NonNull Bitmap originalBitmap) {
        return pixelizeImage(100 / PROGRESS_TO_PIXELIZATION_FACTOR, originalBitmap);
    }

    @Override
    public Bitmap getBitmap(@NonNull Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        /*
         * Checks if enough time has elapsed since the last pixelization call was invoked.
         * This prevents too many pixelization processes from being invoked at the same time
         * while previous ones have not yet completed.
         */
        if ((System.currentTimeMillis() - lastTime) > TIME_BETWEEN_TASKS) {
            lastTime = System.currentTimeMillis();
            int progress = 100 - progressPercent;
            return pixelizeImage(progress / PROGRESS_TO_PIXELIZATION_FACTOR, originalBitmap);
        }
        return currentBitmap;
    }

    /**
     * Selects either the custom pixelization algorithm that sets and gets bitmap
     * pixels manually or the one that uses built-in bitmap operations.
     */
    public Bitmap pixelizeImage(float pixelizationFactor, Bitmap bitmap) {
        return builtInPixelization(pixelizationFactor, bitmap);
    }

    // taken from google's ImagePixalization example.

    /**
     * This method of image pixelization utilizes the bitmap scaling operations built
     * into the framework. By downscaling the bitmap and upscaling it back to its
     * original size (while setting the filter flag to false), the same effect can be
     * achieved with much better performance.
     */
    public Bitmap builtInPixelization(float pixelizationFactor, Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int downScaleFactorWidth = (int) (pixelizationFactor * width);
        downScaleFactorWidth = downScaleFactorWidth > 0 ? downScaleFactorWidth : 1;
        int downScaleFactorHeight = (int) (pixelizationFactor * height);
        downScaleFactorHeight = downScaleFactorHeight > 0 ? downScaleFactorHeight : 1;

        int downScaledWidth = width / downScaleFactorWidth;
        int downScaledHeight = height / downScaleFactorHeight;

        Bitmap pixelatedBitmap = Bitmap.createScaledBitmap(bitmap, downScaledWidth,
                downScaledHeight, false);

        /* Bitmap's createScaledBitmap method has a filter parameter that can be set to either
         * true or false in order to specify either bilinear filtering or point sampling
         * respectively when the bitmap is scaled up or now.
         *
         * Similarly, a BitmapDrawable also has a flag to specify the same thing. When the
         * BitmapDrawable is applied to an ImageView that has some scaleType, the filtering
         * flag is taken into consideration. However, for optimization purposes, this flag was
         * ignored in BitmapDrawables before Jelly Bean MR1.
         *
         * Here, it is important to note that prior to JBMR1, two bitmap scaling operations
         * are required to achieve the pixelization effect. Otherwise, a BitmapDrawable
         * can be created corresponding to the downscaled bitmap such that when it is
         * upscaled to fit the ImageView, the upscaling operation is a lot faster since
         * it uses internal optimizations to fit the ImageView.
         * */

        return Bitmap.createScaledBitmap(pixelatedBitmap, width, height, false);
    }
}
