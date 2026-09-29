package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import eu.bakici.imageprogressbar.utils.IndicatorUtils
import kotlin.math.ceil
import kotlin.random.Random

/** Reveals full-height vertical stripes in a shuffled order. */
class RandomStripeIndicator @JvmOverloads constructor(
    @StripeThickness private val thickness: Int = LEVEL_MEDIUM,
    private val seed: Long = System.nanoTime()
) : ImageIndicator() {

    companion object {
        const val LEVEL_THIN = 4
        const val LEVEL_MEDIUM = 8
        const val LEVEL_THICK = 16

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(LEVEL_THIN, LEVEL_MEDIUM, LEVEL_THICK)
        annotation class StripeThickness
    }

    private val canvas = Canvas()
    private val paint = Paint()
    private var stripes: List<Rect> = emptyList()
    private var order: IntArray = intArrayOf()

    init {
        if (thickness <= 0) {
            throw IllegalArgumentException("Stripe thickness must be positive")
        }
    }

    override fun prepare(original: Bitmap): Bitmap {
        val count = (original.width + thickness - 1) / thickness
        stripes = buildList(count) {
            for (index in 0 until count) {
                val left = index * thickness
                add(Rect(left, 0, minOf(left + thickness, original.width), original.height))
            }
        }
        order = IntArray(count) { it }
        order.shuffle(Random(seed))
        return IndicatorUtils.convertGrayscale(original)
    }

    override fun render(
        original: Bitmap,
        prepared: Bitmap,
        @FloatRange(from = 0.0, to = 1.0) progress: Float
    ): Bitmap {
        if (progress <= 0f) return prepared
        if (progress >= 1f) return original

        val revealedCount = ceil(order.size * progress).toInt()
        val output = IndicatorUtils.createBitmapLike(original)
        canvas.setBitmap(output)
        IndicatorUtils.drawOnBitmap(canvas) {
            drawBitmap(prepared, 0f, 0f, paint)
            for (position in 0 until revealedCount) {
                val stripe = stripes[order[position]]
                drawBitmap(original, stripe, stripe, paint)
            }
        }
        return output
    }
}
