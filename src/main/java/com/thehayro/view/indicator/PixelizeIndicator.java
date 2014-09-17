package com.thehayro.view.indicator;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

public class PixelizeIndicator extends ProgressIndicator {

    private static final long TIME_BETWEEN_TASKS = 400;
    private static final float PROGRESS_TO_PIXELIZATION_FACTOR = 4000.f;
    final Context mContext;

    private Bitmap mPixelatedBitmap;

    private long mLastTime;

    private int mLastProgress;

    public PixelizeIndicator(final Context context) {
        super(IndicationProcessingType.ASYNC);
        mContext = context;
    }

    @Override
    public void onPreProgress(final Bitmap originalBitmap) {

    }

    @Override
    public void onProgress(final Bitmap originalBitmap, final int progressPercent) {
        /**
         * Checks if enough time has elapsed since the last pixelization call was invoked.
         * This prevents too many pixelization processes from being invoked at the same time
         * while previous ones have not yet completed.
         */
        if ((System.currentTimeMillis() - mLastTime) > TIME_BETWEEN_TASKS) {
            mLastTime = System.currentTimeMillis();
            mLastProgress = progressPercent;
            mPixelatedBitmap = pixelizeImage(progressPercent / PROGRESS_TO_PIXELIZATION_FACTOR, originalBitmap).getBitmap();
        }

    }


    /**
     *  Selects either the custom pixelization algorithm that sets and gets bitmap
     *  pixels manually or the one that uses built-in bitmap operations.
     */
    public BitmapDrawable pixelizeImage(float pixelizationFactor, Bitmap bitmap) {
        return builtInPixelization(pixelizationFactor, bitmap);
//        if (mIsBuiltinPixelizationChecked) {
//        } else {
//            return customImagePixelization(pixelizationFactor, bitmap);
//        }
    }

    /**
     * A simple pixelization algorithm. This uses a box blur algorithm where all the
     * pixels within some region are averaged, and that average pixel value is then
     * applied to all the pixels within that region. A higher pixelization factor
     * imposes a smaller number of regions of greater size. Similarly, a smaller
     * pixelization factor imposes a larger number of regions of smaller size.
     */
    public BitmapDrawable customImagePixelization(float pixelizationFactor, Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (mPixelatedBitmap == null || !(width == mPixelatedBitmap.getWidth() && height ==
            mPixelatedBitmap.getHeight())) {
            mPixelatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        int xPixels = (int) (pixelizationFactor * ((float)width));
        xPixels = xPixels > 0 ? xPixels : 1;
        int yPixels = (int)  (pixelizationFactor * ((float)height));
        yPixels = yPixels > 0 ? yPixels : 1;
        int pixel = 0, red = 0, green = 0, blue = 0, numPixels = 0;

        int[] bitmapPixels = new int[width * height];
        bitmap.getPixels(bitmapPixels, 0, width, 0, 0, width, height);

        int[] pixels = new int[yPixels * xPixels];

        int maxX, maxY;

        for (int y = 0; y < height; y+=yPixels) {
            for (int x = 0; x < width; x+=xPixels) {

                numPixels = red = green = blue = 0;

                maxX = Math.min(x + xPixels, width);
                maxY = Math.min(y + yPixels, height);

                for (int i = x; i < maxX; i++) {
                    for (int j = y; j < maxY; j++) {
                        pixel = bitmapPixels[j * width + i];
                        red += Color.red(pixel);
                        green += Color.green(pixel);
                        blue += Color.blue(pixel);
                        numPixels ++;
                    }
                }

                pixel = Color.rgb(red / numPixels, green / numPixels, blue / numPixels);

                Arrays.fill(pixels, pixel);

                int w = Math.min(xPixels, width - x);
                int h = Math.min(yPixels, height - y);

                mPixelatedBitmap.setPixels(pixels, 0 , w, x , y, w, h);
            }
        }

        return new BitmapDrawable(mContext.getResources(), mPixelatedBitmap);
    }

    /**
     * This method of image pixelization utilizes the bitmap scaling operations built
     * into the framework. By downscaling the bitmap and upscaling it back to its
     * original size (while setting the filter flag to false), the same effect can be
     * achieved with much better performance.
     */
    public BitmapDrawable builtInPixelization(float pixelizationFactor, Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int downScaleFactorWidth = (int)(pixelizationFactor * width);
        downScaleFactorWidth = downScaleFactorWidth > 0 ? downScaleFactorWidth : 1;
        int downScaleFactorHeight = (int)(pixelizationFactor * height);
        downScaleFactorHeight = downScaleFactorHeight > 0 ? downScaleFactorHeight : 1;

        int downScaledWidth =  width / downScaleFactorWidth;
        int downScaledHeight = height / downScaleFactorHeight;

        Bitmap pixelatedBitmap = Bitmap.createScaledBitmap(bitmap, downScaledWidth,
            downScaledHeight, false);

        /* Bitmap's createScaledBitmap method has a filter parameter that can be set to either
         * true or false in order to specify either bilinear filtering or point sampling
         * respectively when the bitmap is scaled up or now.
         *
         * Similarly, a BitmapDrawable also has a flag to specify the same thing. When the
         * BitmapDrawable is applied to an ImageView that has some scaleType, the filtering
         * flag is taken into consideration. However, for optimization purposes, this flag was
         * ignored in BitmapDrawables before Jelly Bean MR1.
         *
         * Here, it is important to note that prior to JBMR1, two bitmap scaling operations
         * are required to achieve the pixelization effect. Otherwise, a BitmapDrawable
         * can be created corresponding to the downscaled bitmap such that when it is
         * upscaled to fit the ImageView, the upscaling operation is a lot faster since
         * it uses internal optimizations to fit the ImageView.
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), pixelatedBitmap);
            bitmapDrawable.setFilterBitmap(false);
            return bitmapDrawable;
        } else {
            Bitmap upscaled = Bitmap.createScaledBitmap(pixelatedBitmap, width, height, false);
            return new BitmapDrawable(mContext.getResources(), upscaled);
        }
    }
}
