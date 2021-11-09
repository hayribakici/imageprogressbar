package eu.bakici.imageprogressbar.indicator;

/*
 * Copyright (C) 2021 hayribakici
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

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class SpiralIndicator extends ProgressIndicator {

    private static final int MAX_DEGREE = 1440;
    public static final double PI8 = Math.PI / 180;
    // Distance between spines
    private static final float A = 30f;
    private final Path path;


    private BitmapShader shader;
    private float centerX;
    private float centerY;

    public SpiralIndicator() {
        super();
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
        Bitmap bitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(preProgressBitmap, 0, 0, new Paint());
        archimedeanSpiral(canvas, progressPercent);

        return bitmap;
    }

    private void archimedeanSpiral(Canvas canvas, @FloatRange(from = 0.0, to = 1.0) float progressPercent) {
        double angle = IndicatorUtils.getValueOfPercentD(MAX_DEGREE * PI8, progressPercent);
        Paint paint = new Paint();
        Log.d("spiral", "angle = " + angle);
        canvas.drawBitmap(preProgressBitmap, 0, 0, new Paint());

        float x = (float) (A * angle * Math.cos(angle));
        float y = (float) (A * angle * Math.sin(angle));

        paint.setShader(shader);
        path.lineTo(centerX + x, centerY + y);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
    }
}
