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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import eu.bakici.imageprogressbar.utils.IndicatorUtils;

public class ColorFillIndicator extends ProgressIndicator {

    private final Paint normalPaint;

    /**
     * Type of how the image will be filled.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT,
            PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT,
            PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP,
            PROGRESS_DIRECTION_VERTICAL_TOP_DOWN
    })
    public @interface ProgressDirection {
    }

    /**
     * Lets the progress indication go from left to right.
     */
    public final static int PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT = 0;

    /**
     * Lets the progress indication go from right to left.
     */
    public final static int PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT = 1;

    /**
     * Lets the progress indication go from top to bottom.
     */
    public final static int PROGRESS_DIRECTION_VERTICAL_TOP_DOWN = 2;

    /**
     * Lets the progress indication go from bottom to top.
     */
    public final static int PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP = 3;

    @ProgressDirection
    private final int direction;

    public ColorFillIndicator(@ProgressDirection int direction) {
        super();
        this.direction = direction;
        this.normalPaint = new Paint();
    }


    @Override
    public Bitmap getPreProgressBitmap(final @NonNull Bitmap originalBitmap) {
        return IndicatorUtils.convertGrayscale(originalBitmap);
    }

    @Override
    public Bitmap getBitmap(final @NonNull Bitmap originalBitmap, @FloatRange(from = 0.0, to = 1.0) float progressPercent) {

        final int bitmapHeight = originalBitmap.getHeight();
        final int bitmapWidth = originalBitmap.getWidth();
        final int heightPercent = IndicatorUtils.getValueOfPercent(bitmapHeight, progressPercent);
        final int widthPercent = IndicatorUtils.getValueOfPercent(bitmapWidth, progressPercent);


        Rect bitmapBWRect;
        Rect bitmapSourceRect;
        switch (direction) {
            case PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT:
                bitmapSourceRect = new Rect(0, 0, widthPercent, bitmapHeight);
                bitmapBWRect = new Rect(widthPercent, 0, bitmapWidth, bitmapHeight);
                break;
            case PROGRESS_DIRECTION_HORIZONTAL_RIGHT_LEFT:
                final int complementWidthPercent = bitmapWidth - widthPercent;
                bitmapSourceRect = new Rect(complementWidthPercent, 0, bitmapWidth, bitmapHeight);
                bitmapBWRect = new Rect(0, 0, complementWidthPercent, bitmapHeight);
                break;
            case PROGRESS_DIRECTION_VERTICAL_TOP_DOWN:
                bitmapSourceRect = new Rect(0, 0, bitmapWidth, heightPercent);
                bitmapBWRect = new Rect(0, heightPercent, bitmapWidth, bitmapHeight);
                break;
            case PROGRESS_DIRECTION_VERTICAL_BOTTOM_UP:
                final int complementHeightPercent = bitmapHeight - heightPercent;
                bitmapSourceRect = new Rect(0, complementHeightPercent, bitmapWidth, bitmapHeight);
                bitmapBWRect = new Rect(0, 0, bitmapWidth, complementHeightPercent);
                break;
            default:
                throw new IllegalArgumentException("no valid progress direction specified");
        }

        final Bitmap output = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(output);

        canvas.drawBitmap(preProgressBitmap, bitmapBWRect, bitmapBWRect, normalPaint);
        canvas.drawBitmap(originalBitmap, bitmapSourceRect, bitmapSourceRect, normalPaint);
        return output;
    }


}
