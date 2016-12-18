package eu.bakici.imageprogressbar.indicator;

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

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.IntRange;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class CircularIndicator extends ProgressIndicator {

    private BitmapShader mShader;

    public CircularIndicator() {
        super(SYNC);
    }

    @Override
    public void onPreProgress(final Bitmap originalBitmap) {
        preBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        mShader = new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        currentBitmap = preBitmap;
    }

    @Override
    public void onProgress(final Bitmap source, @IntRange(from = 0, to = 100) int progressPercent) {
        int angle = IndicatorUtils.calcPercent(360, progressPercent);
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(preBitmap, 0, 0, new Paint());
        final RectF arc = new RectF(source.getWidth() * -0.5f, source.getHeight() * -0.5f, source.getWidth() * 1.5f, source.getHeight() * 1.5f);
        paint.setShader(mShader);
        canvas.drawArc(arc, 270, angle, true, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.drawBitmap(source, 0, 0, paint);
        currentBitmap = bitmap;
    }
}
