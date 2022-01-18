/*
 * Copyright (C) 2016, 2021 hayribakici
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
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.CallSuper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Adapter class for Progress indication.
 */
open class Indicator : Parcelable {

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Indicator?> = object : Parcelable.Creator<Indicator?> {
            override fun createFromParcel(source: Parcel): Indicator {
                return Indicator(source)
            }

            override fun newArray(size: Int): Array<Indicator?> {
                return arrayOfNulls(size)
            }
        }
        val TAG = Indicator::class.java.simpleName
    }


    /**
     * @return the current progression.
     */
    open var currentProgressPercent = 0f
        protected set

    /**
     * The current displayed bitmap.
     *
     * @return the current bitmap.
     */
    /**
     * The current bitmap the view is displaying.
     */
    var currentBitmap: Bitmap? = null
        protected set

    /**
     * Standard constructor.
     */
    constructor() {}

    /**
     * The bitmap when onPreProgress is called
     */
    @JvmField
    protected var preProgressBitmap: Bitmap? = null


    protected constructor(`in`: Parcel) {
        currentBitmap = `in`.readParcelable(Bitmap::class.java.classLoader)
        currentProgressPercent = `in`.readFloat()
        preProgressBitmap = `in`.readParcelable(Bitmap::class.java.classLoader)
    }

    fun preProgressBitmap(originalBitmap: Bitmap): Flow<Bitmap?> = flow {
        currentBitmap = getPreProgressBitmap(originalBitmap)
        emit(currentBitmap)
    }

    open fun progressBitmap(state: ProgressState): Flow<Bitmap?> = flow {
        currentBitmap = getBitmap(state)
        emit(currentBitmap)
    }


    /**
     * Called when the progress bat is moving.
     *
     * @param originalBitmap the original bitmap.
     * @param progress       the values in percent. Goes from 0.0 to 1.0.
     * @return the manipulated bitmap that should be displayed based on the percentage of the progress bar.
     */
    open fun getBitmap(state: ProgressState): Bitmap? {
        return currentBitmap
    }

    /**
     * This method is optional. Called once at the beginning before the actual progress is called.
     * This method allows for instance to do some Bitmap manipulation before the progress starts.
     *
     * @param originalBitmap the original bitmap.
     * @return the manipulated bitmap that should be displayed, before the progress starts.
     */
    open fun getPreProgressBitmap(originalBitmap: Bitmap): Bitmap? {
        throw UnsupportedOperationException("onPreProgress is not implemented")
    }

    /**
     * Should be called when the indication is done.
     */
    @CallSuper
    fun cleanUp() {
        currentBitmap = null
    }

    /**
     * This method is optional.
     * Called once at the beginning before the action progress is called. This method
     * allows for instance to do some Bitmap manipulation before the progress starts.
     *
     * @param originalBitmap the original bitmap.
     */
    fun onPreProgress(originalBitmap: Bitmap) {
        preProgressBitmap = getPreProgressBitmap(originalBitmap)
        currentBitmap = preProgressBitmap
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(currentBitmap, flags)
        dest.writeFloat(currentProgressPercent)
        dest.writeParcelable(preProgressBitmap, flags)
    }


}