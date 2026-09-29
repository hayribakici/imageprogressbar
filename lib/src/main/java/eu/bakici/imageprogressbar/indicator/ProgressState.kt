// Legacy pipeline: retained as reference while migrating to ImageIndicator.
// package eu.bakici.imageprogressbar.indicator
//
// import android.graphics.Bitmap
// import android.os.Parcelable
// import androidx.annotation.FloatRange
// import kotlinx.parcelize.Parcelize
//
//
// @Parcelize
// data class ProgressState(val preProgressBitmap: Bitmap? = null, val currentBitmap: Bitmap? = null, val originalBitmap: Bitmap? = null, @FloatRange(from = 0.0, to = 1.0) val progress: Float = 0f) : Parcelable {
//
//     val isMaximum
//         get() = progress == 1f
//
//     val isMinimum
//         get() = progress == 0f
// }
