/*
 * Copyright (C) 2026 hayribakici
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
package eu.bakici.imageprogressbar

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.annotation.FloatRange
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import eu.bakici.imageprogressbar.indicator.EmptyIndicator
import eu.bakici.imageprogressbar.indicator.ImageIndicator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageProgress private constructor(
    owner: LifecycleOwner,
    imageView: ImageView,
    private val original: Bitmap,
    private var imageIndicator: ImageIndicator
) : DefaultLifecycleObserver {
    private var target: ImageView? = imageView
    private var progress = 0f
    private var prepared: Bitmap? = null
    private val updates = Channel<Unit>(Channel.CONFLATED)
    private val lifecycle = owner.lifecycle

    private val worker = owner.lifecycleScope.launch {
        for (update in updates) {
            val requested = progress
            try {
                val bitmap = withContext(Dispatchers.Default) {
                    val preparedBitmap = prepared ?: imageIndicator.prepare(original)
                    if (preparedBitmap !== original) {
                        preparedBitmap.density = original.density
                    }
                    prepared = preparedBitmap
                    val renderedBitmap = imageIndicator.render(original, preparedBitmap, requested)
                    if (renderedBitmap !== original) {
                        renderedBitmap.density = original.density
                    }
                    renderedBitmap
                }
                if (requested == progress) {
                    displayBitmap(bitmap)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.e(
                    "ImageProgress",
                    "Could not render progress; the previous image is retained",
                    error
                )
            }
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    private fun displayBitmap(bitmap: Bitmap) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            target?.setImageBitmap(bitmap)
        }
    }

    /** Requests a complete frame directly at [value], in the range 0..1. */
    @MainThread
    fun setProgress(@FloatRange(from = 0.0, to = 1.0) value: Float) {
        if (target == null) {
            throw IllegalStateException("The image view's lifecycle has ended")
        }
        progress = value
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) updates.trySend(Unit)
    }

    var indicator: ImageIndicator
        get() = this.imageIndicator
        set(value) {
            this.imageIndicator = value
            prepared = null
            updates.trySend(Unit)
        }


    override fun onStart(owner: LifecycleOwner) {
        updates.trySend(Unit)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        worker.cancel()
        updates.close()
        target = null
        lifecycle.removeObserver(this)
    }

    companion object {
        @MainThread
        fun with(owner: LifecycleOwner): Request = Request(owner)
    }

    class Request internal constructor(private val owner: LifecycleOwner) {
        private var indicator: ImageIndicator = EmptyIndicator()

        @MainThread
        fun indicator(indicator: ImageIndicator): Request = apply { this.indicator = indicator }

        /** Call after the view's source bitmap has loaded. */
        @MainThread
        fun into(imageView: ImageView): ImageProgress {
            if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                throw IllegalStateException("Lifecycle has ended")
            }
            val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
            if (bitmap == null || bitmap.isRecycled) {
                throw IllegalArgumentException("ImageView must contain a valid bitmap")
            }

            return ImageProgress(owner, imageView, bitmap, indicator)
        }
    }
}
