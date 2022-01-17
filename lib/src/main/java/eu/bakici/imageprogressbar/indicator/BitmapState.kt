package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap

data class BitmapState(val preProgressBitmap: Bitmap, val currentBitmap: Bitmap?, val originalBitmap: Bitmap)
