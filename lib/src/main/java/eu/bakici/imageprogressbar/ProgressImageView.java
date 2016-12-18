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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import eu.bakici.imageprogressbar.indicator.HybridIndicator;
import eu.bakici.imageprogressbar.indicator.ProgressIndicator;

public class ProgressImageView extends ImageView {

    private static final int MAX_PERCENT = 100;

    private final static String TAG = ProgressImageView.class.getSimpleName();

    private final static String BUNDLE_CURRENT_PROGRESS = TAG + ".bundle.progress";

    private final static String BUNDLE_CURRENT_BITMAP = TAG + ".bundle.bitmap";


    private Bitmap mOriginalBitmap;

    private int mMaximum  = 100;

    private int mProgress;

    private ProgressIndicator mIndicator;

    public ProgressImageView(final Context context) {
        this(context, null);
    }

    public ProgressImageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        if (bm == null) {
            return;
        }
        super.setImageBitmap(bm);
        initOriginalBitmap(bm);
    }

    @Override
    public void setImageDrawable(final Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable == null) {
            return;
        }
        Bitmap bitmap = extractBitmap(drawable);
        if (bitmap != null) {
            initOriginalBitmap(bitmap);
        } else {
            throw new IllegalArgumentException("Drawable does not contain bitmap");
        }
    }

    @Nullable
    private Bitmap extractBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Class<? extends Drawable> drawableClass = drawable.getClass();
            try {
                Method getBitmapMethod = drawableClass.getMethod("getBitmap", null);
                return (Bitmap) getBitmapMethod.invoke(drawable, null);
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
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    private void initOriginalBitmap(final Bitmap bitmap) {
        if (mOriginalBitmap == null) {
            mOriginalBitmap = bitmap;
            fireOnPreProgress();
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
                setImageBitmap(bitmap);
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
        bundle.putInt(BUNDLE_CURRENT_PROGRESS, mProgress);
        if (mIndicator != null) {
            bundle.putParcelable(BUNDLE_CURRENT_BITMAP, mIndicator.getCurrentBitmap());
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
        if (mIndicator == null) {
            return;
        }
        mProgress = progress;
        if (mOriginalBitmap == null) {
            return;
        }
        if (silent) {
            fireOnProgress();
        }
    }


    public int getMaximum() {
        return mMaximum;
    }


    public void setMaximum(final int max) {
        mMaximum = max;
    }

    public int getProgress() {
        return mProgress;
    }


    private int getProgressPercent() {
        final float progressFloat = (float) mProgress;
        final float maxFloat = (float) mMaximum;
        return (int) Math.ceil((progressFloat / maxFloat) * MAX_PERCENT);
    }

    public void setProgressIndicator(final ProgressIndicator progressIndicator) {
        mIndicator = progressIndicator;
        fireOnPreProgress();
    }


    private void fireOnPreProgress() {
        if (mIndicator != null) {
            final int process = mIndicator.getIndicationProcessingType();
            switch (process) {
                case ProgressIndicator.HYBRID:
                case ProgressIndicator.SYNC:
                    mIndicator.onPreProgress(mOriginalBitmap);
                    setImageBitmap(mIndicator.getCurrentBitmap());
                    break;
                case ProgressIndicator.ASYNC:
                    new ProgressImageAsyncTask(mIndicator, this, true).execute(mOriginalBitmap);
                    break;
            }
        }
    }


    private void fireOnProgress() {
        if (mIndicator != null) {
            final int process = mIndicator.getIndicationProcessingType();
            switch (process) {
                case ProgressIndicator.SYNC:
                    mIndicator.onProgress(mOriginalBitmap, getProgressPercent());
                    setImageBitmap(mIndicator.getCurrentBitmap());
                    break;
                case ProgressIndicator.ASYNC:
                    new ProgressImageAsyncTask(mIndicator, this, false).execute(mOriginalBitmap);
                    break;
                case ProgressIndicator.HYBRID:
                    ((HybridIndicator) mIndicator).onProgress(mOriginalBitmap, getProgressPercent(),
                        new HybridIndicator.OnProgressIndicationUpdatedListener() {
                            @Override
                            public void onProgressIndicationUpdated(final Bitmap bitmap) {
                                setImageBitmap(bitmap);
                            }
                        }
                    );
                break;
            }
        }
    }



    public void destroy() {
        if (mIndicator != null) {
            mIndicator.cleanUp();
        }
    }

    private static class ProgressImageAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> {


        private final ProgressIndicator mIndicator;
        private final ProgressImageView mImageView;
        private final boolean mIsPreProgress;

        public ProgressImageAsyncTask(final ProgressIndicator indicator,
                                      final ProgressImageView imageView,
                                      boolean preProgress) {
            mIndicator = indicator;
            mImageView = imageView;
            mIsPreProgress = preProgress;

        }

        @Override
        protected Bitmap doInBackground(final Bitmap... params) {
            final Bitmap bitmap = params[0];
            if (mIsPreProgress) {
                mIndicator.onPreProgress(bitmap);
            } else {
                mIndicator.onProgress(bitmap, mImageView.getProgressPercent());
            }
            return mIndicator.getCurrentBitmap();
        }

        @Override
        protected final void onPostExecute(final Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }
}

