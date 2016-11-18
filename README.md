ImageProgressBar
================

Android Library of a ProgressBar as an image representation. [Download the latest aar.](https://github.com/hayribakici/imageprogressbar/blob/develop/aar/imageprogressbar-1.0.aar)

This is a simple extensible android library that allows you to use an image for a loading indication. There are a couple of build-in indicators such as
 * `BlurIndicator`
   * Indicator that lets the image blur and sharpens it when the progress is running.
 * `ColorFillerIndicator`
   * Indicator that fills the image from black and white to color. The indication can be done from left to right, right to left, top to bottom and bottom to top.
 * `PixelizeIndicator`
   * Indicator that pixelizes the image and sharpens when the progress is running.
 * `CircularIndicator`
   * Indicator that fills the image from black and white to color with a circle animation.
 * `AlphaIndicator`
   * Indicator that fades the image from black and white to color when the progress is running.
 * `RandomBlockIndicator`
   * Indicator that fills the image from black and white to color by randomly adding block-slices of the image in color.

#### Examples

A blur-progress Indicator

![Blur Progress Indicator Example](https://github.com/hayribakici/imageprogressbar/blob/develop/assets/blur_1.png "Blur Progress Indicator")
![Blur Progress Indicator Example](https://github.com/hayribakici/imageprogressbar/blob/develop/assets/blur_2.png "Blur Progress Indicator")

A circulation progress indicator

![Circulation Progress Indicator Example](https://github.com/hayribakici/imageprogressbar/blob/develop/assets/circe_1.png "Circulation Progress Indicator")

#### Bind it in your layout

```xml
<eu.bakici.imageprogressbar.ProgressImageView
        android:id="@+id/image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/sidney" />
```

The indicators are set with the following calls:
```java
ProgressImageView progressImageView = (ProgressImageView) findViewById(R.id.image);
progressImageView.setProgressIndicator(new CircularIndicator());

```

and updated with
```java
progressImageView.setProgress(50));
```

### Build your own indicator

The ProgressImageView is designed to bind various progress indicator representations. This is provided by the class ProgressIndicator.

```java
public abstract class ProgressIndicator {

  public ProgressIndicator(@IndicationProcessingType int indicationProcess) {
          mIndicationProcess = indicationProcess;
      }


/**
* This method is optional.
* Called once at the beginning before the action progress is called. This method
* allows for instance to do some Bitmap manipulation before the progress starts.
*
* @param originalBitmap the original bitmap.
*/
public void onPreProgress(Bitmap originalBitmap) {
  // pre process your bitmap here first
  // (e.g. make grayscale)
}


/**
* Called when the progress bar is moving.
*
* @param originalBitmap  the original bitmap
* @param progressPercent the values in percent. Goes from 0 to 100
*/
public abstract void onProgress(Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent);
// process your bitmap here while the progress is running
```

Inherit from this class and set how your indicator should be run. There are three types on how the ProgressImageView is processing the image manipulation:
1. Synchronous (`SYNC`)
2. Asynchronous (`ASYNC`)
3. Hybrid (`HYBRID`)

###### Synchronous:
As the name implies, the image (pre) processing is done in the main thread. This is useful, if you don't have do to heavy computation with the image. As for the built-in indicators, `ColorFillerIndicator`, `CirculatorIndicator` and `AlphaIndicator` are using the main thread to manipulate the image.

###### Asynchronous:
Also here, as the name implies, the image processing is done by a background thread. The processing is handled by a `AsyncTask`. The `BlurIndicator` and `PixelizeIndicator` are using an `AsyncTask`.

###### Hybrid:
This is a tricky one. Basically it is a synchronous indicator, but with an asynchronous callback. When the progression of the progress becomes jumpy (meaning the progression is not linear), this indicator allows to 'fill the gaps' between the progress jump (e.g. the progress jumps from 1 to 10). It gives you special callback where you can do 'catching up' image manipulation to let the ImageView draw the missing gaps between e.g. 1 and 10. The processing indicator `RandomBlockIndicator` is a `HybridIndicator`.

You need to implement the following method:
```java
/**
 * Same as {@link #onProgress(Bitmap, int)} but with a callback.
 * @param originalBitmap the original bitmap.
 * @param progressPercent the percentage of the current progress.
 * @param listener a callback listener for filling the gaps between progress jumps.
 */
public void onProgress(final Bitmap originalBitmap, @IntRange(from = 0, to = 100) int progressPercent, final OnProgressIndicationUpdatedListener listener) {
    // do you image manipulation here
    // you also get a callback for 'catching up' the progression.
}
```

With the callback interface:
```java
/**
 * Callback interface when the indication has been updated.
 */
public interface OnProgressIndicationUpdatedListener {
    void onProgressIndicationUpdated(final Bitmap bitmap);
}
```
