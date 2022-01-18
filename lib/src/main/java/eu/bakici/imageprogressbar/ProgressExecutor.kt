/*
 * Copyright (C) 2021 hayribakici
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
import androidx.annotation.FloatRange
import eu.bakici.imageprogressbar.indicator.Indicator
import eu.bakici.imageprogressbar.indicator.ProgressState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

/**
 * Helper class.
 */
internal class ProgressExecutor(val indicator: Indicator,
                                private val listener: OnPostExecuteListener<Bitmap?>) {


    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    private val scope: CoroutineScope = MainScope()


    var state: ProgressState = ProgressState();

    var originalBitmap: Bitmap?
        get() = state.originalBitmap
        set(value) {
            state = ProgressState(originalBitmap = value)
        }


    fun prepare() {
        ensureOriginalBitmapExists()
        scope.launch {
            indicator.preProgressBitmap(state.originalBitmap!!)
                    .flowOn(defaultDispatcher)
                    .collect { bitmap ->
                        state = ProgressState(preProgressBitmap = bitmap, originalBitmap)
                        listener.onPostExecute(bitmap)
                    }
        }

    }

    fun start(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        ensureOriginalBitmapExists()
        scope.launch {
            indicator.progressBitmap(null)
                    .flowOn(defaultDispatcher)
                    .onEach { bitmap -> listener.onPostExecute(bitmap) }
                    .collect { bitmap -> listener.onPostExecute(bitmap) }
        }
    }

    private fun ensureOriginalBitmapExists() {
        if (originalBitmap == null) {
            throw IllegalStateException("originalBitmap not set.")
        }
    }


    internal interface OnPostExecuteListener<T> {
        fun onPostExecute(param: T)
    }

}