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
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class CircularIndicator extends ProgressIndicator {


    public static final int CLOCKWISE = 0;
    public static final int COUNTERCLOCKWISE = 1;
    private static final int FULL_CIRCLE = 360;

    /**
     * Type of how the image will be processed.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            CLOCKWISE,
            COUNTERCLOCKWISE
    })
    public @interface Turn {

    }

    private BitmapShader shader;
    private final int turn;

    public CircularIndicator() {
        this(CLOCKWISE);
    }

    public CircularIndicator(@Turn int turn) {
        super(SYNC);
        this.turn = turn;
    }


    @Override
    public void onPreProgress(final @NonNull Bitmap originalBitmap) {
        preProgressBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        shader = new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        currentBitmap = preProgressBitmap;
    }

    @Override
    public void onProgress(final @NonNull Bitmap source, @IntRange(from = 0, to = 100) int progressPercent) {
        int angle = IndicatorUtils.getValueOfPercent(FULL_CIRCLE, progressPercent);
        if (turn == COUNTERCLOCKWISE) {
            angle = angle * (-1);
        }
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(preProgressBitmap, 0, 0, new Paint());
        final RectF arc = new RectF(source.getWidth() * -0.5f,
                source.getHeight() * -0.5f,
                source.getWidth() * 1.5f,
                source.getHeight() * 1.5f);
        paint.setShader(shader);
        canvas.drawArc(arc, 270, angle, true, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.drawBitmap(source, 0, 0, paint);
        currentBitmap = bitmap;
    }
}
