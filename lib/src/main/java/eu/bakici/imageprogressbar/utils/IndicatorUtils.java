package eu.bakici.imageprogressbar.utils;

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
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Helper class with Util functions.
 */
public final class IndicatorUtils {

    public static String HYBRID_THREAD_NAME = "jumper";

    /**
     * Calculates the amount of {@code value} based on {@code percent}.
     *
     * @return the value between [0, value] that is calculated from {@code percent}.
     */
    public static int getValueOfPercent(final int value, final int percent) {
        return Math.round(getValueOfPercentFloat(value, percent));
    }

    public static int getValueOfPercent(final int value, final float percent) {
        return Math.round(value * percent);
    }

    public static double getValueOfPercentD(double value, float percent) {
        return value * percent;
    }

    /**
     * Calculates the amount of {@code value} based on on {@code percent}.
     *
     * @return the value between [0, value] that is calculated from {@code percent}.
     */
    public static float getValueOfPercentFloat(final int value, final int percent) {
        final float p100 = (float) percent * 0.01f;
        return value * p100;
    }

    /**
     * Converts a given {@code source} bitmap into grayscale.
     *
     * @param source the original (colored) bitmap to convert.
     * @return a bitmap in grayscale.
     */
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
