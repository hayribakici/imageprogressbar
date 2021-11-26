/*
 * Copyright (C) 2016, 2021 hayribakici
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

package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import androidx.annotation.FloatRange

class PixelizeIndicator : Indicator() {

    companion object {
        private const val TIME_BETWEEN_TASKS: Long = 400
        private const val PROGRESS_TO_PIXELIZATION_FACTOR = 3000f
    }

    private var lastTime: Long = 0

    override fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap {
        return pixelizeImage(100 / PROGRESS_TO_PIXELIZATION_FACTOR, originalBitmap)
    }

    override fun getBitmap(originalBitmap: Bitmap, @FloatRange(from = 0.0, to = 1.0) progress: Float): Bitmap {
        /*
         * Checks if enough time has elapsed since the last pixelization call was invoked.
         * This prevents too many pixelization processes from being invoked at the same time
         * while previous ones have not yet completed.
         */
        if (System.currentTimeMillis() - lastTime > TIME_BETWEEN_TASKS) {
            lastTime = System.currentTimeMillis()
            val newProgress = (1f - progress) * 100f
            return pixelizeImage(newProgress / PROGRESS_TO_PIXELIZATION_FACTOR, originalBitmap)
        }
        return currentBitmap!!
    }

    /**
     * Selects either the custom pixelization algorithm that sets and gets bitmap
     * pixels manually or the one that uses built-in bitmap operations.
     */
    fun pixelizeImage(pixelizationFactor: Float, bitmap: Bitmap): Bitmap {
        return builtInPixelization(pixelizationFactor, bitmap)
    }
    // taken from google's ImagePixalization example.
    /**
     * This method of image pixelization utilizes the bitmap scaling operations built
     * into the framework. By downscaling the bitmap and upscaling it back to its
     * original size (while setting the filter flag to false), the same effect can be
     * achieved with much better performance.
     */
    fun builtInPixelization(pixelizationFactor: Float, bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var downScaleFactorWidth = (pixelizationFactor * width).toInt()
        downScaleFactorWidth = if (downScaleFactorWidth > 0) downScaleFactorWidth else 1
        var downScaleFactorHeight = (pixelizationFactor * height).toInt()
        downScaleFactorHeight = if (downScaleFactorHeight > 0) downScaleFactorHeight else 1
        val downScaledWidth = width / downScaleFactorWidth
        val downScaledHeight = height / downScaleFactorHeight
        val pixelatedBitmap = Bitmap.createScaledBitmap(bitmap, downScaledWidth,
                downScaledHeight, false)

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
         * */return Bitmap.createScaledBitmap(pixelatedBitmap, width, height, false)
    }


}