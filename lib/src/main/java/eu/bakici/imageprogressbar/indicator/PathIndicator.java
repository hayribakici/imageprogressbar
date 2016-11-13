package eu.bakici.imageprogressbar.indicator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.List;

public class PathIndicator extends ProgressIndicator {


    private final Paint mPaint;
    private List<Point> mPoints;
    private Path mPath;

    public PathIndicator(List<Point> points) {
        super(SYNC);
        mPoints = points;
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void onProgress(Bitmap source, int progressPercent) {
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path = mPath;
        path.moveTo(0, 0);
        for (int i = 0; i < 100; i++) {
            path = new Path();
            path.moveTo(i, 0);
            canvas.drawPath(path, mPaint);
        }
        mCurrentBitmap = bitmap;
    }
}
