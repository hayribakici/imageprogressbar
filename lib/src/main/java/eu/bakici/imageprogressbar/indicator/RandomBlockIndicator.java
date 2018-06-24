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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Collections;


/**
 * Indicator that fills the image by randomly placing colored blocks of the image.
 */
public class RandomBlockIndicator extends BlockIndicator {

    public RandomBlockIndicator() {
        this(BLOCK_SIZE_MEDIUM);
    }

    public RandomBlockIndicator(final int pixels) {
        super(pixels);
    }

    @Override
    protected void onPostBlockInitialization() {
        Collections.shuffle(blocks);
    }

}
