/*
 * Copyright (C) 2016, 2022, 2026 hayribakici
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

package eu.bakici.imageprogressbar.indicator

import android.graphics.Bitmap
import androidx.annotation.FloatRange

/**
 * Defines the appearance of an image at an absolute progress value.
 *
 * Each image session owns a separate instance. The controller calls [prepare] and
 * [render] sequentially, off the main thread. Implementations only perform image
 * processing; the controller owns scheduling and delivery.
 * Calls need not run on the same background thread.
 *
 * This is the contract for the new rendering pipeline. Legacy indicator
 * implementations are commented out as reference until migrated.
 */
abstract class ImageIndicator {

    /**
     * Prepares an image before its first render and returns its starting appearance.
     * The default starting appearance is the original image.
     *
     * Cache geometry or other image-dependent resources here. This method is called
     * again when the source changes or the session resets, and must replace any
     * previous preparation state. Random geometry must remain stable between
     * renders; accept a seed in the implementation's configuration if that geometry
     * must also be reproducible after restoration.
     *
     * Do not modify or recycle [original]. The returned bitmap must have the same
     * dimensions as [original] and remain unchanged while the session uses it.
     */
    open fun prepare(original: Bitmap): Bitmap = original

    /**
     * Returns the complete appearance at [progress], from 0 (start) to 1 (complete).
     *
     * The controller supplies a finite value in this range, the current source,
     * and the bitmap returned by [prepare] for that source. Progress may jump,
     * decrease or repeat. Rendering must not depend on previously rendered frames:
     * the same prepared session and progress must produce the same appearance.
     * For example, a block indicator draws all blocks revealed at this progress,
     * rather than adding one block per call.
     *
     * Return a bitmap with the original dimensions. Do not modify or recycle the
     * inputs or previously returned bitmaps: a view may still be displaying them.
     * Returning an unchanged input bitmap is allowed.
     */
    abstract fun render(
        original: Bitmap,
        prepared: Bitmap,
        @FloatRange(from = 0.0, to = 1.0) progress: Float
    ): Bitmap
}
