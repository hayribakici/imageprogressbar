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
package eu.bakici.imageprogressbar;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import eu.bakici.imageprogressbar.indicator.Indicator;
import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class ProgressImageView extends ImageView implements ImageProgressBarViewModel.OnPostExecuteListener<Bitmap> {

    private final static String TAG = ProgressImageView.class.getSimpleName();

    private final static String PROGRESS_STATE = TAG + "progressState";


    private Bitmap originalBitmap;

    private int maximum = 100;

    private int progress;
    private Indicator indicator;
    private boolean fromSuper = false;
    private ImageProgressBarViewModel executor;

    public ProgressImageView(final Context context) {
        this(context, null);
    }

    public ProgressImageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Calls {@code super.setImageDrawable(Drawable drawable)}
     *
     * @param bm the bitmap to set.
     */
    private void superSetImageBitmap(final Bitmap bm) {
        fromSuper = true;
        // since super.setImageBitmap() has a optimized way to
        // call setImageDrawable() and not to run into a
        // OOM, we have this flag fromSuper to just indicate
        // that the new bitmap should be drawn by the imageview with no proper
        // ProgressImageView manipulation.
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(final Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable == null || fromSuper) {
            fromSuper = false;
            return;
        }
        Bitmap bitmap = extractBitmap(drawable);
        if (bitmap != null) {
            // it is important to store the bitmap that should be displayed to enable the
            // proper image manipulation
            originalBitmap = bitmap;
            fireOnPreProgress();
        } else {
            throw new IllegalArgumentException("Drawable does not contain bitmap");
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        originalBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        fireOnPreProgress();
    }

    @Nullable
    private Bitmap extractBitmap(@NonNull Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Class<? extends Drawable> drawableClass = drawable.getClass();
        try {
            // this is hacky and ugly. There is a possibility to get a custom drawable
            // that is not part of the framework but stores a bitmap.
            // Therefore we check if a method named 'getBitmap' exists.
            // and we return the value of it to initialize the original bitmap for
            // proper image manipulation.
            Method getBitmapMethod = drawableClass.getMethod("getBitmap");
            return (Bitmap) getBitmapMethod.invoke(drawable);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Indicator indicator = bundle.getParcelable(PROGRESS_STATE);
            if (indicator != null) {
                this.indicator = indicator;
                setProgress(IndicatorUtils.integerizePercent(indicator.getCurrentProgressPercent()));
                superSetImageBitmap(indicator.getCurrentBitmap());
            }
//            final int progressPercent = bundle.getInt(BUNDLE_CURRENT_PROGRESS, 0);
//            setProgress(progressPercent, false);
//            final Bitmap bitmap = bundle.getParcelable(BUNDLE_CURRENT_BITMAP);
//            if (bitmap != null) {
//                superSetImageBitmap(bitmap);
//            }
            super.onRestoreInstanceState(bundle.getParcelable("super_state"));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("super_state", super.onSaveInstanceState());
        if (indicator != null) {
            bundle.putParcelable(PROGRESS_STATE, indicator);
        }
        return bundle;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    public void setProgress(final int progress) {
        setProgress(progress, true);
    }

    private void setProgress(final int progress, final boolean silent) {
        if (indicator == null) {
            return;
        }
        if (originalBitmap == null) {
            return;
        }
        this.progress = progress;
        if (silent) {
            fireOnProgress();
        }
    }


    public int getMaximum() {
        return maximum;
    }


    public void setMaximum(final int max) {
        maximum = max;
    }

    public int getProgress() {
        return progress;
    }


    @FloatRange(from = 0.0, to = 1.0)
    private float getProgressPercent() {
        return (float) progress / maximum;
    }

    public void setProgressIndicator(final Indicator indicator) {

        fireOnPreProgress();
    }


    private void fireOnPreProgress() {
        if (indicator != null) {
            if (executor == null) {
                executor = new ImageProgressBarViewModel(originalBitmap, indicator, this);
            }
            executor.prepare();
        }
    }


    private void fireOnProgress() {
        if (indicator != null) {
            if (executor == null) {
                executor = new ImageProgressBarViewModel(originalBitmap, indicator, this);
            }
            executor.start(getProgressPercent());
        }
    }

    /**
     * Frees memory. Needs to be called when
     */
    private void destroy() {

    }

    @Override
    public void onPostExecute(Bitmap param) {
        superSetImageBitmap(param);
    }
}



