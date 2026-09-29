/*
 * Copyright (C) 2016, 2022 hayribakici
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
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import eu.bakici.imageprogressbar.utils.IndicatorUtils


class CircularIndicator(@Turn private val turn: Int = CLOCKWISE) : ImageIndicator() {

    companion object {
        const val CLOCKWISE = 0
        const val COUNTERCLOCKWISE = 1
        private const val FULL_CIRCLE = 360
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [CLOCKWISE, COUNTERCLOCKWISE])
    annotation class Turn

    private val coloredPaint = Paint()
    private val grayscalePaint = Paint()
    private val arc = RectF()
    private val canvas = Canvas()

    override fun prepare(original: Bitmap): Bitmap {
        coloredPaint.shader = BitmapShader(original, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        // Extend the oval beyond the image so the sweep reaches every corner.
        arc.set(
            original.width * -0.5f,
            original.height * -0.5f,
            original.width * 1.5f,
            original.height * 1.5f
        )
        val grayscale = IndicatorUtils.convertGrayscale(original)
        grayscalePaint.shader =
            BitmapShader(grayscale, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        return grayscale
    }

    override fun render(
        original: Bitmap,
        prepared: Bitmap,
        @FloatRange(from = 0.0, to = 1.0) progress: Float
    ): Bitmap {
        if (progress == 0f) return prepared
        if (progress == 1f) return original

        val fullSweep =
            if (turn == COUNTERCLOCKWISE) -FULL_CIRCLE.toFloat() else FULL_CIRCLE.toFloat()
        val coloredSweep = fullSweep * progress
        // The remaining sector sweeps in the opposite direction from twelve o'clock.
        val grayscaleSweep = coloredSweep - fullSweep
        val output = IndicatorUtils.createBitmapLike(original)
        canvas.setBitmap(output)
        IndicatorUtils.drawOnBitmap(canvas) {
            drawArc(arc, 270f, grayscaleSweep, true, grayscalePaint)
            drawArc(arc, 270f, coloredSweep, true, coloredPaint)
        }
        return output
    }
}
