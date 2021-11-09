package eu.bakici.imageprogressbar.indicator;

/*
 * Copyright (C) 2016 hayribakici
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

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class CircularIndicator extends ProgressIndicator {


    public static final int CLOCKWISE = 0;
    public static final int COUNTERCLOCKWISE = 1;
    protected static final int FULL_CIRCLE = 360;

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

    private final Paint paint;
    private BitmapShader coloredShader;
    private BitmapShader bwShader;
    private RectF arc;

    private final int turn;

    public CircularIndicator() {
        this(CLOCKWISE);
    }

    public CircularIndicator(@Turn int turn) {
        super();
        this.turn = turn;
        paint = new Paint();
    }


    @Override
    public Bitmap getPreProgressBitmap(@NonNull Bitmap originalBitmap) {
        coloredShader = new BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        arc = new RectF(originalBitmap.getWidth() * -0.5f,
                originalBitmap.getHeight() * -0.5f,
                originalBitmap.getWidth() * 1.5f,
                originalBitmap.getHeight() * 1.5f);
        Bitmap bwBitmap = IndicatorUtils.convertGrayscale(originalBitmap);
        bwShader = new BitmapShader(bwBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        return bwBitmap;
    }


    @Override
    public Bitmap getBitmap(@NonNull Bitmap originalBitmap, @FloatRange(from = 0.0, to = 1.0) float progressPercent) {
        int angle = IndicatorUtils.getValueOfPercent(FULL_CIRCLE, progressPercent);
        if (turn == COUNTERCLOCKWISE) {
            angle = angle * (-1);
        }
        Bitmap bitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // drawing the colored arc with its counter arc
        drawArc(canvas, 270, (FULL_CIRCLE - angle) * (-1), bwShader);
        drawArc(canvas, 270, angle, coloredShader);
        return bitmap;
    }

    private void drawArc(@NonNull Canvas canvas, int startAngle, int angle, BitmapShader shader) {
        Paint paint = new Paint();
        paint.setShader(shader);
        canvas.drawArc(arc, startAngle, angle, true, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
    }
}
