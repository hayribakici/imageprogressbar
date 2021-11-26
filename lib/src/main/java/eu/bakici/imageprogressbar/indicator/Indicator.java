package eu.bakici.imageprogressbar.indicator;

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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.CallSuper;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Adapter class for Progress indication.
 */
public class Indicator implements Parcelable {

    public static final Creator<Indicator> CREATOR = new Creator<Indicator>() {
        @Override
        public Indicator createFromParcel(Parcel source) {
            return new Indicator(source);
        }

        @Override
        public Indicator[] newArray(int size) {
            return new Indicator[size];
        }
    };

    protected float currentProgressPercent;
    static final String TAG = Indicator.class.getSimpleName();

    /**
     * The current bitmap the view is displaying.
     */
    @Nullable
    protected Bitmap currentBitmap;

    /**
     * Standard constructor.
     */
    public Indicator() {
    }

    /**
     * The bitmap when onPreProgress is called
     */
    protected Bitmap preProgressBitmap;


    protected Indicator(@NonNull Parcel in) {
        this.currentBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.currentProgressPercent = in.readFloat();
        this.preProgressBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    /**
     * Called when the progress bat is moving.
     *
     * @param originalBitmap the original bitmap.
     * @param progress       the values in percent. Goes from 0.0 to 1.0.
     * @return the manipulated bitmap that should be displayed based on the percentage of the progress bar.
     */
    @Nullable
    public Bitmap getBitmap(@NonNull Bitmap originalBitmap, @FloatRange(from = 0.0, to = 1.0) float progress) {
        return getCurrentBitmap();
    }

    /**
     * This method is optional. Called once at the beginning before the actual progress is called.
     * This method allows for instance to do some Bitmap manipulation before the progress starts.
     *
     * @param originalBitmap the original bitmap.
     * @return the manipulated bitmap that should be displayed, before the progress starts.
     */
    public Bitmap getPreProgressBitmap(@NonNull Bitmap originalBitmap) {
        throw new UnsupportedOperationException("onPreProgress is not implemented");
    }


    /**
     * The current displayed bitmap.
     *
     * @return the current bitmap.
     */
    @Nullable
    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    /**
     * @return the current progression.
     */
    public final float getCurrentProgressPercent() {
        return currentProgressPercent;
    }

    public Number getProgressValue(float progress) {
        return 0;
    }

    /**
     * Should be called when the indication is done.
     */
    @CallSuper
    public void cleanUp() {
        currentBitmap = null;
    }

    /**
     * This method is optional.
     * Called once at the beginning before the action progress is called. This method
     * allows for instance to do some Bitmap manipulation before the progress starts.
     *
     * @param originalBitmap the original bitmap.
     */
    public final void onPreProgress(@NonNull Bitmap originalBitmap) {
        preProgressBitmap = getPreProgressBitmap(originalBitmap);
        currentBitmap = preProgressBitmap;
    }

    public void onProgress(@NonNull Bitmap originalBitmap, @FloatRange(from = 0.0, to = 1.0) float progressPercent) {
        currentBitmap = getBitmap(originalBitmap, progressPercent);
        currentProgressPercent = progressPercent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(this.currentBitmap, flags);
        dest.writeFloat(this.currentProgressPercent);
        dest.writeParcelable(this.preProgressBitmap, flags);
    }
}
