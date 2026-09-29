package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import eu.bakici.imageprogressbar.utils.IndicatorUtils
import eu.bakici.imageprogressbar.utils.IndicatorUtils.convertGrayscale

/** Reveals the colored image from the top-left corner along a diagonal boundary.  */
class DiagonalIndicator : ImageIndicator() {
    private val canvas = Canvas()
    private val path = Path()
    private val paint = Paint()

    override fun prepare(original: Bitmap): Bitmap {
        return convertGrayscale(original)
    }

    override fun render(original: Bitmap, prepared: Bitmap, progress: Float): Bitmap {
        if (progress <= 0f) return prepared
        if (progress >= 1f) return original

        val threshold = progress * 2f
        val width = original.width.toFloat()
        val height = original.height.toFloat()

        path.reset()
        path.moveTo(0f, 0f)
        if (threshold <= 1f) {
            path.lineTo(width * threshold, 0f)
            path.lineTo(0f, height * threshold)
        } else {
            path.lineTo(width, 0f)
            path.lineTo(width, height * (threshold - 1f))
            path.lineTo(width * (threshold - 1f), height)
            path.lineTo(0f, height)
        }
        path.close()

        val output = IndicatorUtils.createBitmapLike(original)
        canvas.setBitmap(output)
        IndicatorUtils.drawOnBitmap(canvas) {
            drawBitmap(prepared, 0f, 0f, paint)
            save()
            clipPath(path)
            drawBitmap(original, 0f, 0f, paint)
            restore()
        }
        return output
    }
}
