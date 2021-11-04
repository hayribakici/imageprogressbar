package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class SpiralIndicator extends ProgressIndicator {

    private static final int MAX_DEGREE = 1440;
    private static final float A = 1.1f;
    // see http://oeis.org/A072895
    private static final int B = 281;
    private final Path path;

    private BitmapShader shader;
    private float centerX;
    private float centerY;

    public SpiralIndicator() {
        super(ASYNC);
        path = new Path();
    }

    @Override
    public Bitmap getPreProgressBitmap(@NonNull Bitmap originalBitmap) {
        shader = new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        centerX = originalBitmap.getWidth() * 0.5f;
        centerY = originalBitmap.getHeight() * 0.5f;
        path.moveTo(centerX, centerY);
        return IndicatorUtils.convertGrayscale(originalBitmap);
    }


    @Override
    public Bitmap getBitmap(@NonNull Bitmap originalBitmap, @FloatRange(from = 0.0, to = 1.0) float progressPercent) {
        int step = IndicatorUtils.getValueOfPercent(B, progressPercent);


        Bitmap bitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        float x =
//        path.lineTo(centerX );

        return bitmap;
    }

    private void archimedeanSpiral(Canvas canvas, float progressPercent) {
        float angle = IndicatorUtils.getValueOfPercent(MAX_DEGREE, progressPercent);
        Paint paint = new Paint();

        canvas.drawBitmap(preProgressBitmap, 0, 0, new Paint());

        float x = (float) ((A * angle * Math.cos(angle)));
        float y = (float) ((A * angle * Math.sin(angle)));
        final RectF arc = new RectF(centerX - x, centerY - y,
                centerX + x, centerY + y);
        paint.setShader(shader);
        path.lineTo(centerX + x, centerY + y);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
    }
}
