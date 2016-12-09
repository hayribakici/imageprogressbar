package eu.bakici.imageprogressbar.indicator;

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
 */

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Base class for Progress indication.
 */
public class ProgressIndicator implements Parcelable {

    static final String TAG = ProgressIndicator.class.getSimpleName();

    /**
     * Type of how the image will be processed.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            SYNC,
            ASYNC,
            HYBRID
    })
    public @interface IndicationProcessingType {
    }

    /**
     * Synchronous processing, the image processing will be done
     * on the main thread.
     */
    public static final int SYNC = 1;

    /**
     * Ansynchronous processing, the image processing will be done
     * on a seperate thread (AsyncTask)
     */
    public static final int ASYNC = 2;

    /**
     * A mixture of synchronous and asynchronous. Which means, that
     * there are calculations that are done in a seperate thread while these
     * are push to the main thread.
     */
    public static final int HYBRID = 3;

    /**
     * The current bitmap the view is displaying.
     */
    @Nullable
    protected Bitmap mCurrentBitmap;

    /**
     * The type of processing this indicator is running on.
     */
    @IndicationProcessingType
    private int mIndicationProcess;

    /**
     * The bitmap when onPreProgress is called
     */
    protected Bitmap mPreBitmap;

    /**
     * Standard constructor. Initializes a ProgressIndicator instance.
     *
     * @param indicationProcess the type of processing this indicator should have.
     */
    public ProgressIndicator(@IndicationProcessingType int indicationProcess) {
        mIndicationProcess = indicationProcess;
    }


    /**
     * This method is optional. Called once at the beginning before the actual progress is called.
     * This method allows for instance to do some Bitmap manipulation before the progress starts.
     * @param originalBitmap the original bitmap.
     * @return the manipulated bitmap that should be displayed, before the progress starts.
     */
    public Bitmap createPreProgressBitmap(Bitmap originalBitmap) {
        throw new UnsupportedOperationException("onPreProgress is not implemented");
    }

    /**
     * Called when the progress bat is moving.
     * @param originalBitmap the original bitmap.
     * @param progressPercent the values in percent. Goes from 0 to 100.
     * @return the manipulated bitmap that should be displayed based on the percentage of the progress bar.
     */
    public Bitmap createBitmapOnProgress(Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        return null;
    }

    /**
     * This method is optional.
     * Called once at the beginning before the action progress is called. This method
     * allows for instance to do some Bitmap manipulation before the progress starts.
     *
     * @param originalBitmap the original bitmap.
     */
    public void onPreProgress(Bitmap originalBitmap) {
        mPreBitmap = createPreProgressBitmap(originalBitmap);
        mCurrentBitmap = mPreBitmap;
    }


    /**
     * Called when the progress bar is moving.
     *
     * @param originalBitmap  the original bitmap
     * @param progressPercent the values in percent. Goes from 0 to 100
     */
    public void onProgress(Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        mCurrentBitmap = createBitmapOnProgress(originalBitmap, progressPercent);
    }

    /**
     * The current displayed bitmap.
     *
     * @return the current bitmap.
     */
    @Nullable
    public Bitmap getCurrentBitmap() {
        return mCurrentBitmap;
    }

    /**
     * Should be called when the indication is done.
     */
    @CallSuper
    public void cleanUp() {
        mCurrentBitmap = null;
    }

    /**
     * @return The indicator processing type this indicator is running on.
     * @see IndicationProcessingType
     */
    @IndicationProcessingType
    public int getIndicationProcessingType() {
        return mIndicationProcess;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mCurrentBitmap, flags);
        dest.writeInt(this.mIndicationProcess);
        dest.writeParcelable(this.mPreBitmap, flags);
    }

    @SuppressWarnings("all")
    protected ProgressIndicator(Parcel in) {
        this.mCurrentBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.mIndicationProcess = in.readInt();
        this.mPreBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ProgressIndicator> CREATOR = new Creator<ProgressIndicator>() {
        @Override
        public ProgressIndicator createFromParcel(Parcel source) {
            return new ProgressIndicator(source);
        }

        @Override
        public ProgressIndicator[] newArray(int size) {
            return new ProgressIndicator[size];
        }
    };
}
