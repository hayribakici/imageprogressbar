package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import eu.bakici.imageprogressbar.utils.IndicatorUtils
import kotlin.math.ceil

/** Shared grid setup and rendering for indicators that reveal rectangular blocks. */
abstract class BlockIndicator(
    @BlockSize private val blockSize: Int = BLOCK_SIZE_MEDIUM
) : ImageIndicator() {

    companion object {
        const val BLOCK_SIZE_BIG = 60
        const val BLOCK_SIZE_MEDIUM = 50
        const val BLOCK_SIZE_SMALL = 30
        const val BLOCK_SIZE_EXTRA_SMALL = 20

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(BLOCK_SIZE_BIG, BLOCK_SIZE_MEDIUM, BLOCK_SIZE_SMALL, BLOCK_SIZE_EXTRA_SMALL)
        annotation class BlockSize
    }

    private val canvas = Canvas()
    private val paint = Paint()
    private var blocks: List<Rect> = emptyList()
    private var order: IntArray = intArrayOf()

    init {
        if (blockSize <= 0) {
            throw IllegalArgumentException("Block size must be positive")
        }
    }

    final override fun prepare(original: Bitmap): Bitmap {
        val columns = (original.width + blockSize - 1) / blockSize
        val rows = (original.height + blockSize - 1) / blockSize
        blocks = buildList(columns * rows) {
            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    val left = column * blockSize
                    val top = row * blockSize
                    add(
                        Rect(
                            left,
                            top,
                            minOf(left + blockSize, original.width),
                            minOf(top + blockSize, original.height)
                        )
                    )
                }
            }
        }
        order = IntArray(columns * rows) { it }
        orderBlocks(order, columns, rows)
        return IndicatorUtils.convertGrayscale(original)
    }

    /** Reorder the initially row-major block indexes in [order] for this indicator. */
    protected abstract fun orderBlocks(order: IntArray, columns: Int, rows: Int)

    final override fun render(
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
                val rect = blocks[order[position]]
                drawBitmap(original, rect, rect, paint)
            }
        }
        return output
    }
}
