# ImageProgressBar

Android Library of a ProgressBar as an image representation.

This is a simple extensible android library that allows you to use an image for a loading indication. There are a couple of build-in indicators such as

<table>
    <tr>
        <th>Indicator</th>
        <th>Description</th>
        <th>Image</th>
    </tr>
    <tr>
        <td><code>AlphaIndicator</code></td>
        <td>Indicator that fades the image from black and white to color when the progress is running.</td>
    </tr>
    <tr>
        <td><code>BlurIndicator</code></td>
        <td>Indicator that lets the image blur and sharpens it when the progress is running.</td>
    </tr>
    <tr>
        <td>
            <code>CircularIndicator</code>
        </td>
        <td>Indicator that fills the image from black and white to color with a circle animation.</td>
    <tr>
    <tr>
        <td>
            <code>ColorFillerIndicator</code></td>
        <td>Indicator that fills the image from black and white to color. The indication can be done from
          left to right, right to left, top to bottom and bottom to top.
        </td>
    </tr>
    <tr>
        <td><code>DiagonalIndicator</code></td>
        <td>Indicator that fills the image from black and white to color going diagonal. From left to
          right, right to left, top to bottom and bottom to top.</td>
    </tr>
    <tr>
        <td><code>RandomBlockIndicator</code></td>
        <td>Indicator that fills the image from black and white to color by randomly adding block-slices
          of the image in color.</td>
    </tr>
    <tr>
        <td><code>RandomStripeIndicator</code></td>
        <td>Indicator that fills the image from black and white to color by randomly adding slices of the
          image in color.</td>
    </tr>
    <tr>
        <td><code>SnakeIndicator</code></td>
        <td>Indicator that fills the image blockwise like a Snake-game.</td>
    </tr>
    <tr>
        <td><code>SpiralBlockIndicator</code></td>
        <td>Indicator that fills the image blockwise from the center to the borders.</td>
    </tr>
    <tr>
        <td><code>SpiralIndicator</code></td>
        <td>Indicator that fills the image from black and white to color with a spiral animation.</td>
    </tr>
</table>

## Get it or [Download the latest aar.](./aar/imageprogressbar-2.0.aar)

Add to your root `build.gradle`:

```groovy
    allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

And then the dependency

```groovy
dependencies {
    compile 'com.github.hayribakici:imageprogressbar:3.0'
}
```

## Bind your `ImageView`

```kotlin

class MyActivity() : AppCompatActivity() {

  lateinit var imgProgress: ImageProgress
  
  fun onCreate(savedInstance: Bundle) {
    setContenView(R.layout.main)
    imgProgress = ImageProgress.with(this)
                                  // can be changed on runtime
                                  .indicator(CircularIndicator())
                                  .with(findViewById(R.id.image))
  }
}                              
```

and call somewhere

```kotlin
// accepts values between 0 and 1
imgProgress.progress = 0.1
```


# Build your own indicator

This library is designed to bind various indicator representations. This is provided by the `ImageIndicator` class.

Inherit from this class and implement the following methods:

```kotlin
class MyIndicator : ImageIndicator() {

  
  fun prepare(original: Bitmap): Bitmap {
    // optional for pre processing, e.g. making the image black and white, warp ... 
  }

  fun render(original: Bitmap, prepared: Bitmap, @FloatRange(from = 0.0, to = 1.0) progress: Float): Bitmap {
    // implement this method to change the image based on the progression of `progress`
  }
}
```



## Changelog

See [Changelog](Changelog.md)

## Licence

Apache Licence
