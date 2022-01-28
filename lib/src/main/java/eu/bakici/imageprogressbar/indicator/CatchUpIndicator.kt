/*
 * Copyright (C) 2016,2022 hayribakici
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
package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import eu.bakici.imageprogressbar.utils.IndicatorUtils.floatPercent
import eu.bakici.imageprogressbar.utils.IndicatorUtils.integerizePercent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * An indicator that is used when the progression should respect 'progress jumps' (e.g. from 4 to 89).
 * This indicator then 'catches up' the progress values in between (emits the values 5, 6,...,88).
 */
abstract class CatchUpIndicator : Indicator() {

    /**
     * Helper
     */
    private var currentProgress = 0f

    override fun progressBitmap(state: ProgressState): Flow<Bitmap?> {
        return flow {
            val currProgress = integerizePercent(currentProgress)
            val p = integerizePercent(state.progress)
            if (currProgress < p - 1) {
                // large progressbar jump
                val diff = p - currProgress
                for (i in 1..diff) {
                    val missingProgressPercent = currProgress + i
                    val newState = ProgressState(state.preProgressBitmap, state.currentBitmap, state.originalBitmap, floatPercent(missingProgressPercent))
                    emit(getBitmap(newState))
                }
                currentProgress = state.progress
            }
            emit(getBitmap(state))
        }
    }


    /**
     * Callback interface when the indication has been updated.
     */
    interface OnProgressIndicationUpdatedListener {
        fun onProgressIndicationUpdated(bitmap: Bitmap?)
    }
}