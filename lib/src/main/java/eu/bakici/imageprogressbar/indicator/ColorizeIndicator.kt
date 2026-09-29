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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.FloatRange
import eu.bakici.imageprogressbar.utils.IndicatorUtils.convertGrayscale
import eu.bakici.imageprogressbar.utils.IndicatorUtils.createBitmapLike
import eu.bakici.imageprogressbar.utils.IndicatorUtils.drawOnBitmap
import eu.bakici.imageprogressbar.utils.IndicatorUtils.getValueOfPercent

/**
 * Indicator that shows the image in black in white and slowly gets its color once
 * the progress is running.
 */
class ColorizeIndicator : ImageIndicator() {
    companion object {
        private const val MAX_ALPHA = 255
    }

    private val alphaPaint: Paint = Paint()
    private val canvas = Canvas()

    override fun prepare(original: Bitmap): Bitmap = convertGrayscale(original)

    override fun render(
        original: Bitmap,
        prepared: Bitmap,
        @FloatRange(from = 0.0, to = 1.0) progress: Float
    ): Bitmap {

        val output = createBitmapLike(original)
        canvas.setBitmap(output)
        alphaPaint.alpha = getValueOfPercent(MAX_ALPHA, progress)
        drawOnBitmap(canvas) {
            drawBitmap(prepared, 0f, 0f, Paint())
            drawBitmap(original, 0f, 0f, alphaPaint)
        }
        return output
    }


}
