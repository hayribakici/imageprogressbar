package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap

/** Indicator that just returns the original bitmap */
internal class EmptyIndicator : ImageIndicator() {
    override fun render(original: Bitmap, prepared: Bitmap, progress: Float): Bitmap = original
}