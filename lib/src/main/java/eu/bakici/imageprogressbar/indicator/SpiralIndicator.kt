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

package eu.bakici.imageprogressbar.indicator

import android.graphics.*
import androidx.annotation.FloatRange
import eu.bakici.imageprogressbar.utils.IndicatorUtils
import java.lang.StrictMath.cos
import java.lang.StrictMath.sin

/**
 * Indicator that draws the colored image as a spiral.
 */
class SpiralIndicator : CatchUpIndicator() {

    companion object {
        private const val MAX_DEGREE = 1440
        private const val PI8 = Math.PI / 180

        // Distance between spines
        private const val A = 30f
    }

    private val path: Path = Path()
    private val paint: Paint = Paint()
    private var shader: BitmapShader? = null
    private var centerX = 0f
    private var centerY = 0f

    override fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap {
        shader = BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        centerX = originalBitmap.width * 0.5f
        centerY = originalBitmap.height * 0.5f
        path.moveTo(centerX, centerY)
        return IndicatorUtils.convertGrayscale(originalBitmap)
    }

    override fun getBitmap(state: ProgressState): Bitmap {
        val originalBitmap = state.originalBitmap!!
        val bitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(state.preProgressBitmap!!, 0f, 0f, paint)
        drawArchimedeanSpiral(canvas, state.progress)
        return bitmap
    }

    private fun drawArchimedeanSpiral(canvas: Canvas, @FloatRange(from = 0.0, to = 1.0) progress: Float) {
        val angle = IndicatorUtils.getValueOfPercentD(MAX_DEGREE * PI8, progress)
        val paint = Paint()
        val x = (A * angle * cos(angle)).toFloat()
        val y = (A * angle * sin(angle)).toFloat()
        paint.shader = shader
        path.lineTo(centerX + minOf(x, canvas.width.toFloat()), centerY + minOf(y, canvas.height.toFloat()))
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
    }

}