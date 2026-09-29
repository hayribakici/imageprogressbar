package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import eu.bakici.imageprogressbar.utils.IndicatorUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IndicatorBitmapSizingTest {
    @Test
    fun grayscalePreparationPreservesSourceSizeDensityAndBounds() {
        val original = Bitmap.createBitmap(101, 67, Bitmap.Config.ARGB_8888).apply {
            density = 480
            eraseColor(Color.RED)
            setPixel(width - 1, height - 1, Color.GREEN)
        }

        val prepared = IndicatorUtils.convertGrayscale(original)

        assertEquals(original.width, prepared.width)
        assertEquals(original.height, prepared.height)
        assertEquals(original.density, prepared.density)
        assertEquals(
            Color.alpha(original.getPixel(100, 66)),
            Color.alpha(prepared.getPixel(100, 66))
        )
        assertEquals(Color.red(prepared.getPixel(100, 66)), Color.green(prepared.getPixel(100, 66)))
        assertEquals(
            Color.green(prepared.getPixel(100, 66)),
            Color.blue(prepared.getPixel(100, 66))
        )
    }

    @Test
    fun indicatorPreparationAndFramesKeepTheSourceDimensionsAndDensity() {
        val original = Bitmap.createBitmap(101, 67, Bitmap.Config.ARGB_8888).apply {
            density = 480
            eraseColor(Color.rgb(80, 140, 220))
        }
        val indicators = listOf(
            ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT),
            CircularIndicator(),
            ColorizeIndicator(),
            DiagonalIndicator(),
            PixelizeIndicator(),
            RandomBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL, seed = 1L),
            RandomStripeIndicator(seed = 1L),
            SnakeIndicator(BlockIndicator.BLOCK_SIZE_SMALL),
            SpiralBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL),
            SpiralIndicator()
        )

        indicators.forEach { indicator ->
            val prepared = indicator.prepare(original)
            assertBitmapMatchesSource(
                "${indicator.javaClass.simpleName} prepared",
                original,
                prepared
            )

            val rendered = indicator.render(original, prepared, 0.5f)
            assertBitmapMatchesSource(
                "${indicator.javaClass.simpleName} rendered",
                original,
                rendered
            )
        }
    }

    private fun assertBitmapMatchesSource(label: String, original: Bitmap, actual: Bitmap) {
        assertEquals("$label width", original.width, actual.width)
        assertEquals("$label height", original.height, actual.height)
        assertEquals("$label density", original.density, actual.density)
    }
}
