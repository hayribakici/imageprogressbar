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

package eu.bakici.imageprogressbar.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import kotlin.math.roundToInt


/**
 * Helper class with Util functions.
 */
object IndicatorUtils {

    /**
     * Draws onto the canvas's attached bitmap and detaches it afterward, even if drawing throws.
     * Attach the output before calling; do not nest calls or share the canvas between concurrent renders.
     */
    inline fun drawOnBitmap(canvas: Canvas, draw: Canvas.() -> Unit) {
        try {
            canvas.draw()
        } finally {
            canvas.setBitmap(null)
        }
    }

    /**
     * Calculates the amount of `value` based on `percent`.
     *
     * @return the value between [0, value] that is calculated from `percent`.
     */
    @JvmStatic
    fun getValueOfPercent(value: Int, percent: Int): Int =
        getValueOfPercentFloat(value, percent).roundToInt()


    @JvmStatic
    fun getValueOfPercent(value: Int, percent: Float): Int = (value * percent).roundToInt()

    fun getValueOfPercentD(value: Double, percent: Float): Double = value * percent

    /**
     * Calculates the amount of `value` based on on `percent`.
     *
     * @return the value between [0, value] that is calculated from `percent`.
     */
    private fun getValueOfPercentFloat(value: Int, percent: Int): Float {
        val p100 = percent.toFloat() * 0.01f
        return value * p100
    }

    @JvmStatic
    fun integerizePercent(@FloatRange(from = 0.0, to = 1.0) percent: Float): Int =
        (percent * 100).toInt()

    @JvmStatic
    fun floatPercent(@IntRange(from = 0, to = 100) percent: Int): Float = percent.toFloat() / 100

    /**
     * Converts a given `source` bitmap into grayscale.
     *
     * @param source the original (colored) bitmap to convert.
     * @return a bitmap in grayscale.
     */
    @JvmStatic
    fun convertGrayscale(source: Bitmap): Bitmap {
        val output = createBitmapLike(source)
        val canvas = Canvas(output)
        val paint = Paint()
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(source, 0f, 0f, paint)
        return output
    }

    fun createCanvasFromBitmap(source: Bitmap): Canvas {
        val bitmap = createBitmapLike(source)
        return Canvas(bitmap)
    }

    /** Creates a same-size bitmap with matching density so Canvas draws are not density-scaled. */
    @JvmStatic
    fun createBitmapLike(source: Bitmap): Bitmap =
        Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888).apply {
            density = source.density
        }
}
