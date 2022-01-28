/*
 * Copyright (C) 2016, 2022 hayribakici
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
import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.parcelize.Parcelize

/**
 * Adapter class for Progress indication.
 */
@Parcelize
open class Indicator : Parcelable {

    companion object {
        val TAG = Indicator::class.java.simpleName
    }

    fun preProgressBitmap(originalBitmap: Bitmap): Flow<Bitmap?> = flow {
        emit(getPreProgressBitmap(originalBitmap))
    }

    open fun progressBitmap(state: ProgressState): Flow<Bitmap?> = flow {
        emit(getBitmap(state))
    }

    internal fun restore(state: ProgressState): Flow<Bitmap?> =
            flow {
                emit(getPreProgressBitmap(state.originalBitmap!!))
                emit(getBitmap(state))
            }

    /**
     * Called when the progress bat is moving.
     *
     * @param originalBitmap the original bitmap.
     * @param progress       the values in percent. Goes from 0.0 to 1.0.
     * @return the manipulated bitmap that should be displayed based on the percentage of the progress bar.
     */
    open fun getBitmap(state: ProgressState): Bitmap? = state.currentBitmap

    /**
     * This method is optional. Called once at the beginning before the actual progress is called.
     * This method allows for instance to do some Bitmap manipulation before the progress starts.
     *
     * @param originalBitmap the original bitmap.
     * @return the manipulated bitmap that should be displayed, before the progress starts.
     */
    open fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap? = originalBitmap

}