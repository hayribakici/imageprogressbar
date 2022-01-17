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
import android.graphics.Rect
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import eu.bakici.imageprogressbar.utils.IndicatorUtils


class ColorFillIndicator(@ProgressDirection private val direction: Int) : Indicator() {

    companion object {
        /**
         * Lets the progress indication go from left to right.
         */
        const val PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT = 0

        /**
         * Lets the progress indication go from right to left.
         */
        const val PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT = 1

        /**
         * Lets the progress indication go from top to bottom.
         */
        const val PROGRESS_DIRECTION_VERTICAL_TOP_DOWN = 2

        /**
         * Lets the progress indication go from bottom to top.
         */
        const val PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP = 3
    }

    /**
     * Type of how the image will be filled.
     */
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT, PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT, PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP, PROGRESS_DIRECTION_VERTICAL_TOP_DOWN])
    annotation class ProgressDirection

    private val normalPaint: Paint = Paint()

    override fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap {
        return IndicatorUtils.convertGrayscale(originalBitmap)
    }

    override fun getBitmap(bitmaps: BitmapState, @FloatRange(from = 0.0, to = 1.0) progress: Float): Bitmap {
        val originalBitmap = bitmaps.originalBitmap
        val bitmapHeight = originalBitmap.height
        val bitmapWidth = originalBitmap.width
        val heightPercent = IndicatorUtils.getValueOfPercent(bitmapHeight, progress)
        val widthPercent = IndicatorUtils.getValueOfPercent(bitmapWidth, progress)
        val bitmapBWRect: Rect
        val bitmapSourceRect: Rect
        when (direction) {
            PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT -> {
                bitmapSourceRect = Rect(0, 0, widthPercent, bitmapHeight)
                bitmapBWRect = Rect(widthPercent, 0, bitmapWidth, bitmapHeight)
            }
            PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT -> {
                val complementWidthPercent = bitmapWidth - widthPercent
                bitmapSourceRect = Rect(complementWidthPercent, 0, bitmapWidth, bitmapHeight)
                bitmapBWRect = Rect(0, 0, complementWidthPercent, bitmapHeight)
            }
            PROGRESS_DIRECTION_VERTICAL_TOP_DOWN -> {
                bitmapSourceRect = Rect(0, 0, bitmapWidth, heightPercent)
                bitmapBWRect = Rect(0, heightPercent, bitmapWidth, bitmapHeight)
            }
            PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP -> {
                val complementHeightPercent = bitmapHeight - heightPercent
                bitmapSourceRect = Rect(0, complementHeightPercent, bitmapWidth, bitmapHeight)
                bitmapBWRect = Rect(0, 0, bitmapWidth, complementHeightPercent)
            }
            else -> throw IllegalArgumentException("no valid progress direction specified")
        }
        val output = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawBitmap(bitmaps.preProgressBitmap, bitmapBWRect, bitmapBWRect, normalPaint)
        canvas.drawBitmap(originalBitmap, bitmapSourceRect, bitmapSourceRect, normalPaint)
        return output
    }


}