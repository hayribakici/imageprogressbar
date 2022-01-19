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
import eu.bakici.imageprogressbar.utils.IndicatorUtils.convertGrayscale
import eu.bakici.imageprogressbar.utils.IndicatorUtils.getValueOfPercent


/**
 * Indicator that shows the image in black in white and slowly gets its color once
 * the progress is running.
 */
class ColorizeIndicator : Indicator() {
    companion object {
        private const val MAX_ALPHA = 255
    }

    private val alphaPaint: Paint = Paint()

    override fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap = convertGrayscale(originalBitmap)

    override fun getBitmap(state: ProgressState): Bitmap {
        val originalBitmap = state.originalBitmap!!
        val output = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        alphaPaint.alpha = getValueOfPercent(MAX_ALPHA, state.progress)
        canvas.drawBitmap(state.preProgressBitmap!!, 0f, 0f, Paint())
        canvas.drawBitmap(originalBitmap, 0f, 0f, alphaPaint)
        return output
    }


}