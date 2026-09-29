package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import androidx.annotation.FloatRange
import eu.bakici.imageprogressbar.utils.IndicatorUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

/** Reveals the colored image along an expanding Archimedean spiral. */
class SpiralIndicator : ImageIndicator() {
    companion object {
        private const val TURNS = 4f
        private const val SAMPLES = 512
    }

    private val path = Path()
    private val canvas = Canvas()
    private val grayscalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val spiralPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var shader: BitmapShader? = null
    private var centerX = 0f
    private var centerY = 0f
    private var maxRadius = 0f

    override fun prepare(original: Bitmap): Bitmap {
        centerX = original.width * 0.5f
        centerY = original.height * 0.5f
        // Extend slightly beyond the farthest corner so the final turn reaches the image.
        maxRadius = hypot(centerX, centerY) * 1.15f
        shader = BitmapShader(original, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        spiralPaint.shader = shader
        spiralPaint.style = Paint.Style.STROKE
        spiralPaint.strokeWidth = max(6f, minOf(original.width, original.height) * 0.12f)
        spiralPaint.strokeCap = Paint.Cap.ROUND
        return IndicatorUtils.convertGrayscale(original)
    }

    override fun render(
        original: Bitmap,
        prepared: Bitmap,
        @FloatRange(from = 0.0, to = 1.0) progress: Float
    ): Bitmap {
        if (progress <= 0f) return prepared
        if (progress >= 1f) return original

        path.reset()
        val maxAngle = TURNS * 2f * PI.toFloat() * progress
        for (sample in 0..SAMPLES) {
            val fraction = sample.toFloat() / SAMPLES
            val angle = maxAngle * fraction
            val radius = maxRadius * fraction
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            if (sample == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        val output = IndicatorUtils.createBitmapLike(original)
        canvas.setBitmap(output)
        IndicatorUtils.drawOnBitmap(canvas) {
            drawBitmap(prepared, 0f, 0f, grayscalePaint)
            drawPath(path, spiralPaint)
        }
        return output
    }
}
