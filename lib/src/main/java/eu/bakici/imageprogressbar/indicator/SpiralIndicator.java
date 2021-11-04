package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class SpiralIndicator extends ProgressIndicator {

    private static final int MAX_DEGREE = 900;
    private static final float A = 1.1f;

    private BitmapShader shader;
    private float centerX;
    private float centerY;

    public SpiralIndicator() {
        super(SYNC);
    }

    @Override
    public Bitmap getPreProgressBitmap(@NonNull Bitmap originalBitmap) {
        shader = new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        centerX = originalBitmap.getWidth() * 0.5f;
        centerY = originalBitmap.getHeight() * 0.5f;
        return IndicatorUtils.convertGrayscale(originalBitmap);
    }


    @Override
    public Bitmap getBitmap(@NonNull Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent) {
        float angle = IndicatorUtils.getValueOfPercentFloat(MAX_DEGREE, progressPercent) % 360;
        Log.d("a", String.format("%s %%, angle %s", progressPercent, angle));
        int h = Math.max(originalBitmap.getWidth(), originalBitmap.getHeight());
        float a = (float) h / 24f;
        Bitmap bitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
//        canvas.drawBitmap(preProgressBitmap, 0, 0, new Paint());

        final RectF arc = new RectF(centerX, centerY,
                (float) (centerX + (A * angle * Math.cos(angle))),
                (float) (centerY + (A * angle * Math.sin(angle))));
        paint.setShader(shader);
//        canvas.drawLine(centerX, centerY, (float) (centerX + A * angle * Math.cos(angle)), (float) (centerY + A * angle * Math.sin(angle)), paint);
        canvas.drawArc(arc, 270, angle, true, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
//        canvas.drawBitmap(originalBitmap, 0, 0, paint);
        return bitmap;
    }
}
