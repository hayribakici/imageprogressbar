package eu.bakici.imageprogressbar;

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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import eu.bakici.imageprogressbar.indicator.HybridIndicator;
import eu.bakici.imageprogressbar.indicator.ProgressIndicator;

public class ProgressImageView extends ImageView implements OnPostExecuteListener<Bitmap> {

    private static final int MAX_PERCENT = 100;

    private final static String TAG = ProgressImageView.class.getSimpleName();

    private final static String BUNDLE_CURRENT_PROGRESS = TAG + ".bundle.progress";

    private final static String BUNDLE_CURRENT_BITMAP = TAG + ".bundle.bitmap";

    private final static String INDICATOR = TAG + "indicator";


    private Bitmap originalBitmap;

    private int maximum = 100;

    private int progress;
    private ProgressIndicator indicator;
    private boolean fromSuper = false;

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
        } else {
            Class<? extends Drawable> drawableClass = drawable.getClass();
            try {
                // this is hacky and ugly. There is a possibility to get a custom drawable
                // that is not part of the framework but stores a bitmap.
                // Therefore we check if a method named 'getBitmap' exists.
                // and we return the value of it to initialize the original bitmap for
                // proper image manipulation.
                Method getBitmapMethod = drawableClass.getMethod("getBitmap");
                return (Bitmap) getBitmapMethod.invoke(drawable);
            } catch (NoSuchMethodException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            final int progressPercent = bundle.getInt(BUNDLE_CURRENT_PROGRESS, 0);
            setProgress(progressPercent, false);
            final Bitmap bitmap = bundle.getParcelable(BUNDLE_CURRENT_BITMAP);
            if (bitmap != null) {
                superSetImageBitmap(bitmap);
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
        bundle.putInt(BUNDLE_CURRENT_PROGRESS, progress);
        if (indicator != null) {
            bundle.putParcelable(BUNDLE_CURRENT_BITMAP, indicator.getCurrentBitmap());
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
        this.progress = progress;
        if (originalBitmap == null) {
            return;
        }
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


    private int getProgressPercent() {
        final float progressFloat = (float) progress;
        final float maxFloat = (float) maximum;
        return (int) Math.ceil((progressFloat / maxFloat) * MAX_PERCENT);
    }

    public void setProgressIndicator(final ProgressIndicator progressIndicator) {
        indicator = progressIndicator;
        fireOnPreProgress();
    }


    private void fireOnPreProgress() {
        if (indicator != null) {
            final int process = indicator.getIndicationProcessingType();
            switch (process) {
                case ProgressIndicator.HYBRID:
                case ProgressIndicator.SYNC:
                    indicator.onPreProgress(originalBitmap);
                    superSetImageBitmap(indicator.getCurrentBitmap());
                    break;
                case ProgressIndicator.ASYNC:
                    new ProgressImageAsyncTask(indicator, getProgressPercent(), true, this).execute(originalBitmap);
                    break;
            }
        }
    }


    private void fireOnProgress() {
        if (indicator != null) {
            final int process = indicator.getIndicationProcessingType();
            switch (process) {
                case ProgressIndicator.SYNC:
                    indicator.onProgress(originalBitmap, getProgressPercent());
                    superSetImageBitmap(indicator.getCurrentBitmap());
                    break;
                case ProgressIndicator.ASYNC:
                    new ProgressImageAsyncTask(indicator, getProgressPercent(), false, this).execute(originalBitmap);
                    break;
                case ProgressIndicator.HYBRID:
                    ((HybridIndicator) indicator).onProgress(originalBitmap, getProgressPercent(),
                            new HybridIndicator.OnProgressIndicationUpdatedListener() {
                                @Override
                                public void onProgressIndicationUpdated(final Bitmap bitmap) {
                                    superSetImageBitmap(bitmap);
                                }
                            }
                    );
                    break;
            }
        }
    }

    /**
     * Frees memory. Needs to be called when
     */
    private void destroy() {
        if (indicator != null) {
            indicator.cleanUp();
        }
    }

    @Override
    public void onPostExecute(Bitmap param) {
        setImageBitmap(param);
    }

    static class ProgressImageAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> {


        private final ProgressIndicator indicator;
        private final int progressPercent;
        private final boolean isPreProgress;
        private final OnPostExecuteListener<Bitmap> listener;

        public ProgressImageAsyncTask(final ProgressIndicator indicator,
                                      final int progressPercent,
                                      boolean preProgress,
                                      OnPostExecuteListener<Bitmap> listener) {
            this.indicator = indicator;
            this.progressPercent = progressPercent;
            this.isPreProgress = preProgress;
            this.listener = listener;

        }

        @Override
        protected Bitmap doInBackground(final Bitmap... params) {
            final Bitmap bitmap = params[0];
            if (isPreProgress) {
                indicator.onPreProgress(bitmap);
            } else {
                indicator.onProgress(bitmap, progressPercent);
            }
            return indicator.getCurrentBitmap();
        }

        @Override
        protected final void onPostExecute(final Bitmap bitmap) {
            listener.onPostExecute(bitmap);
        }


    }


}

interface OnPostExecuteListener<T> {
    void onPostExecute(T param);
}

