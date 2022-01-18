package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import android.util.Log
import eu.bakici.imageprogressbar.utils.IndicatorUtils.floatPercent
import eu.bakici.imageprogressbar.utils.IndicatorUtils.integerizePercent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/*
 * Copyright (C) 2016 Hayri Bakici
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
 */ /**
 * An indicator that is a synchronous indicator at its core, but does now and then gives asynchronous
 * callbacks. This is good, if the progress percents becomes jumpy, meaning there is not linear
 * increase of the progress. This indicator is there to 'fill the gaps' and let progression
 * catch on from where the progress jump started until its desired progression.
 */
abstract class CatchUpIndicator : Indicator() {

    override fun progressBitmap(state: ProgressState): Flow<Bitmap?> {
        return flow {
            val currProgress = integerizePercent(currentProgressPercent)
            val p = integerizePercent(state.progress)
            if (currProgress < p - 1) {
                // large progressbar jump
                val diff = p - currProgress
                Log.d("Ketchup", "diff: $diff")
                for (i in 1..diff) {
                    val missingProgressPercent = currProgress + i
                    val newState = ProgressState(state.preProgressBitmap, state.currentBitmap, state.originalBitmap, floatPercent(missingProgressPercent))
                    emit(getBitmap(newState))
                }
                currentProgressPercent = state.progress
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