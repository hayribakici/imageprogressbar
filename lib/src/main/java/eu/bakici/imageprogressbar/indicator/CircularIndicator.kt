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

import android.graphics.*
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import eu.bakici.imageprogressbar.utils.IndicatorUtils


class CircularIndicator(@Turn private val turn: Int = CLOCKWISE) : Indicator() {

    companion object {
        const val CLOCKWISE = 0
        const val COUNTERCLOCKWISE = 1
        private const val FULL_CIRCLE = 360
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [CLOCKWISE, COUNTERCLOCKWISE])
    annotation class Turn

    private var coloredShader: BitmapShader? = null
    private var bwShader: BitmapShader? = null
    private var arc: RectF? = null

    override fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap {
        coloredShader = BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        // stretching the canvas of the arc
        arc = RectF(originalBitmap.width * -0.5f,
                originalBitmap.height * -0.5f,
                originalBitmap.width * 1.5f,
                originalBitmap.height * 1.5f)
        val bwBitmap = IndicatorUtils.convertGrayscale(originalBitmap)
        bwShader = BitmapShader(bwBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        return bwBitmap
    }

    override fun getBitmap(state: ProgressState): Bitmap {
        val progress = state.progress
        var angle = IndicatorUtils.getValueOfPercent(FULL_CIRCLE, progress)
        if (turn == COUNTERCLOCKWISE) {
            angle = -angle
        }
        val originalBitmap = state.originalBitmap!!
        val bitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // drawing the colored arc with its counter arc
        drawArc(canvas, -(FULL_CIRCLE - angle), bwShader!!)
        drawArc(canvas, angle, coloredShader!!)
        return bitmap
    }

    private fun drawArc(@NonNull canvas: Canvas, angle: Int, @NonNull shader: BitmapShader) {
        val paint = Paint()
        paint.shader = shader
        canvas.drawArc(arc!!, 270f, angle.toFloat(), true, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
    }

}