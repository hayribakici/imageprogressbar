package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.annotation.IntRange;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class CircularIndicator extends ProgressIndicator {

    private BitmapShader mShader;

    public CircularIndicator() {
        super(SYNC);
    }



    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        mPreBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        mShader = new BitmapShader(originalBitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mCurrentBitmap = mPreBitmap;
    }

    @Override
    public void onProgress(final Bitmap source, @IntRange(from = 0, to = 100) int progressPercent) {
        int angle = IndicatorUtils.calcPercent(360, progressPercent);
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(mPreBitmap, 0, 0, new Paint());
        final RectF arc = new RectF(source.getWidth() * -0.5f, source.getHeight() * -0.5f, source.getWidth() * 1.5f, source.getHeight() * 1.5f);
        paint.setShader(mShader);
        canvas.drawArc(arc, 270, angle, true, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.drawBitmap(source, 0, 0, paint);
        mCurrentBitmap = bitmap;
    }
}
