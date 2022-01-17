/*
 * Copyright (C) 2016 hayribakici
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

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.FloatRange
import eu.bakici.imageprogressbar.utils.IndicatorUtils.getValueOfPercent

class BlurIndicator(private val context: Context) : Indicator() {

    companion object {
        private const val MAX_RADIUS = 25
    }

    override fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap {
        return Blur.fastblur(context, originalBitmap, MAX_RADIUS)
    }

    @Synchronized
    override fun getBitmap(bitmaps: BitmapState, @FloatRange(from = 0.0, to = 1.0) progress: Float): Bitmap {
        if (progress == 100f) {
            return bitmaps.originalBitmap
        }
        val radius = MAX_RADIUS - getValueOfPercent(MAX_RADIUS, progress)
        if (radius <= 0) {
            // insanity check
            return bitmaps.originalBitmap
        }

        return Blur.fastblur(context, bitmaps.originalBitmap, radius)
    }


}