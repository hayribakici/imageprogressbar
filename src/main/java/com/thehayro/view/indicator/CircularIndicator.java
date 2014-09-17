package com.thehayro.view.indicator;

import com.thehayro.view.utils.IndicatorUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;

public class CircularIndicator extends ProgressIndicator {

    private Bitmap mPreBitmap;

    private BitmapShader mShader;

    public CircularIndicator(final IndicationProcessingType indicationProcess) {
        super(indicationProcess);
    }



    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        mPreBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        mShader = new BitmapShader(originalBitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mCurrentBitmap = mPreBitmap;
    }

    @Override
    public void onProgress(final Bitmap source, final int progressPercent) {
        int angle = IndicatorUtils.calcPercent(360, progressPercent);
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(mPreBitmap, 0, 0, new Paint());
        RectF arc = new RectF(0, 0, source.getWidth(), source.getHeight());
//        paint.setShader(mShader);
        canvas.drawArc(arc, 270, angle, true, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.drawBitmap(source, 0, 0, paint);
        mCurrentBitmap = bitmap;
    }
}
