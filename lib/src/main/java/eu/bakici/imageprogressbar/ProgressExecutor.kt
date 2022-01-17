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
import android.os.Handler
import android.os.Looper
import androidx.annotation.FloatRange
import eu.bakici.imageprogressbar.indicator.Indicator
import eu.bakici.imageprogressbar.utils.IndicatorUtils.integerizePercent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Helper class.
 */
internal class ProgressExecutor(private val originalBitmap: Bitmap,
                                var indicator: Indicator,
                                private val listener: OnPostExecuteListener<Bitmap?>) {


    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    private val scope: CoroutineScope = MainScope()
    private var preProgressBitmap: Bitmap? = null


    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())


    fun prepare() {
        scope.launch {
            indicator.preProgressBitmap(originalBitmap)
                    .flowOn(defaultDispatcher)
                    .collect { bitmap ->
                        preProgressBitmap = bitmap
                        listener.onPostExecute(bitmap)
                    }
        }

    }

    fun start(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        scope.launch {
            indicator.progressBitmap(preProgressBitmap!!, originalBitmap, progress)
                    .flowOn(defaultDispatcher)
                    .onEach { bitmap -> listener.onPostExecute(bitmap) }
                    .collect { bitmap -> listener.onPostExecute(bitmap) }
        }
    }


    private fun catchUp(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        val currProgress = integerizePercent(indicator.currentProgressPercent)
        val p = integerizePercent(progress)
        if (currProgress < p - 1) {
            // large progressbar jump
            val diff = p - currProgress
            for (i in 1..diff) {
                val missingProgressPercent = currProgress + i
//                indicator.onProgress(originalBitmap, floatPercent(missingProgressPercent))
                callback()
            }
        }
    }

    private fun callback() {
        mainThreadHandler.post { listener.onPostExecute(indicator.currentBitmap) }
    }

    internal interface OnPostExecuteListener<T> {
        fun onPostExecute(param: T)
    }

}