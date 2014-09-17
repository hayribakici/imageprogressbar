package com.thehayro.view.indicator;

import com.thehayro.view.HelloAndroidActivity;
import com.thehayro.view.utils.IndicatorUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class BlurIndicator extends ProgressIndicator {

    private static final int MAX_RADIUS = 25;

    private final Context mContext;

    public BlurIndicator(final Context context) {
        super(IndicationProcessingType.ASYNC);
        mContext = context;
    }

    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        mCurrentBitmap = Blur.fastblur(mContext, originalBitmap, MAX_RADIUS);
    }

    @Override
    public void onProgress(final Bitmap originalBitmap, final int progressPercent) {

        if (progressPercent == 100) {
            mCurrentBitmap = originalBitmap;
        }
        final int radius = MAX_RADIUS - IndicatorUtils.calcPercent(MAX_RADIUS, progressPercent);
        if (radius <= 0) {
            // insanity check
            mCurrentBitmap = originalBitmap;
        }
        Log.d(HelloAndroidActivity.TAG, "snapshot = " + radius);
        mCurrentBitmap = Blur.fastblur(mContext, originalBitmap, radius);
    }

}
