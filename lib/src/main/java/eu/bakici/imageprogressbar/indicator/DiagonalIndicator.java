package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class DiagonalIndicator extends ProgressIndicator {

    private final Point a;
    private final Point b;
    private final Point c;
    private BitmapShader shader;

    public DiagonalIndicator() {
        this.a = new Point();
        this.b = new Point();
        this.c = new Point();
    }

    @Override
    public Bitmap getPreProgressBitmap(@NonNull Bitmap originalBitmap) {
        shader = new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        return IndicatorUtils.convertGrayscale(originalBitmap);
    }

    @Override
    public Bitmap getBitmap(@NonNull Bitmap originalBitmap, @FloatRange(from = 0.0, to = 1.0) float progressPercent) {

        final int bitmapHeight = 2 * originalBitmap.getHeight();
        final int bitmapWidth = 2 * originalBitmap.getWidth();
        final int heightPercent = IndicatorUtils.getValueOfPercent(bitmapHeight, progressPercent);
        final int widthPercent = IndicatorUtils.getValueOfPercent(bitmapWidth, progressPercent);


        Rect bitmapBWRect;
        Rect bitmapSourceRect;

        bitmapSourceRect = new Rect(0, 0, widthPercent, bitmapHeight);
        bitmapBWRect = new Rect(widthPercent, 0, bitmapWidth, bitmapHeight);


        final Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
//        paint.setStrokeWidth(4);
//        paint.setColor(android.graphics.Color.RED);
//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        paint.setAntiAlias(true);
        paint.setShader(shader);
        a.set(0, bitmapHeight - heightPercent);
        b.set(widthPercent * 2, bitmapHeight / 2);
        c.set(0, bitmapHeight / 2);
//        Point a = new Point(0, 0);
//        Point b = new Point(0, 100);
//        Point c = new Point(87, 50);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(c.x, c.y);
        path.lineTo(b.x, b.y);
//        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();


        canvas.drawBitmap(preProgressBitmap, 0, 0, new Paint());
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        return output;
    }
}