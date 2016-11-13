package eu.bakici.imageprogressbar.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public final class IndicatorUtils {

    public static int calcPercent(final int value, final int percent) {
        final float p = (float) percent;
        final float p100 = p / 100;
        return Math.round(value * p100);
    }

    public static Bitmap convertGrayscale(final Bitmap source) {
        final int width = source.getWidth();
        final int height = source.getHeight();
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(source, 0, 0, paint);
        return output;
    }

    public static Canvas createCanvasFromBitmap(Bitmap source) {
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        return new Canvas(bitmap);
    }
}
