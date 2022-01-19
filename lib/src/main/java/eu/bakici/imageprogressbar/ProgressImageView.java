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
import eu.bakici.imageprogressbar.indicator.ProgressState;

public class ProgressImageView extends ImageView implements ProgressViewModel.OnPostExecuteListener<Bitmap> {

    private final static String TAG = ProgressImageView.class.getSimpleName();

    private final static String INDICATOR = TAG + ".indicator";
    private final static String STATE = TAG + ".state";


    private int maximum = 100;

    private int progress;

    private boolean fromSuper = false;
    private final ProgressViewModel viewModel;

    public ProgressImageView(final Context context) {
        this(context, null);
    }

    public ProgressImageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        viewModel = new ProgressViewModel(this);
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
            viewModel.setOriginalBitmap(bitmap);
            fireOnPreProgress();
        } else {
            throw new IllegalArgumentException("Drawable does not contain bitmap");
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        viewModel.setOriginalBitmap(((BitmapDrawable) getDrawable()).getBitmap());
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
            Indicator indicator = bundle.getParcelable(INDICATOR);
            if (indicator != null) {
                viewModel.setIndicator(indicator);
            }
            ProgressState progressState = bundle.getParcelable(STATE);
            if (progressState != null) {
                viewModel.restore(progressState);
//                setProgress(IndicatorUtils.integerizePercent(progressState.getProgress()));
//                superSetImageBitmap(progressState.getCurrentBitmap());

            }
            super.onRestoreInstanceState(bundle.getParcelable("super_state"));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("super_state", super.onSaveInstanceState());
        bundle.putParcelable(INDICATOR, viewModel.getIndicator());
        bundle.putParcelable(STATE, viewModel.getState());
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
        viewModel.setIndicator(indicator);
        fireOnPreProgress();
    }


    private void fireOnPreProgress() {
        viewModel.prepare();
    }


    private void fireOnProgress() {
        viewModel.start(getProgressPercent());
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



