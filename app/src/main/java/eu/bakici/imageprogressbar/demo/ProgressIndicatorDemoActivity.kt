/*
 * Copyright (C) 2016, 2026 hayribakici
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

package eu.bakici.imageprogressbar.demo


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import eu.bakici.imageprogressbar.ImageProgress
import eu.bakici.imageprogressbar.indicator.BlockIndicator
import eu.bakici.imageprogressbar.indicator.BlurIndicator
import eu.bakici.imageprogressbar.indicator.CircularIndicator
import eu.bakici.imageprogressbar.indicator.ColorFillIndicator
import eu.bakici.imageprogressbar.indicator.ColorizeIndicator
import eu.bakici.imageprogressbar.indicator.DiagonalIndicator
import eu.bakici.imageprogressbar.indicator.PixelizeIndicator
import eu.bakici.imageprogressbar.indicator.RandomBlockIndicator
import eu.bakici.imageprogressbar.indicator.RandomStripeIndicator
import eu.bakici.imageprogressbar.indicator.SnakeIndicator
import eu.bakici.imageprogressbar.indicator.SpiralBlockIndicator
import eu.bakici.imageprogressbar.indicator.SpiralIndicator
import eu.bakici.imageprogressbar.utils.IndicatorUtils

class ProgressIndicatorDemoActivity : AppCompatActivity() {
    private lateinit var progressImageView: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var radioImageLoader: RadioGroup
    private lateinit var startButton: Button
    private lateinit var imageProgress: ImageProgress
    private var optionId = -1
    private var state = false

    private val autoProgressRunnable: Runnable = object : Runnable {
        override fun run() {
            val progress = seekBar.progress
            if (progress >= seekBar.max) {
                startButton.setText(R.string.start)
                state = false
                return
            }
            seekBar.progress = progress + 1
            if (state) {
                seekBar.postDelayed(this, 300)
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressImageView = findViewById(R.id.image)
        imageProgress = ImageProgress.with(this).into(progressImageView);

        seekBar = findViewById(R.id.progress_bar)
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                imageProgress.setProgress(IndicatorUtils.floatPercent(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        startButton = findViewById(R.id.button)
        startButton.setOnClickListener { view: View ->
            state = !state
            if (state) {
                (view as Button).setText(R.string.stop)
            } else {
                (view as Button).setText(R.string.start)
            }
            view.post(autoProgressRunnable)
        }
        radioImageLoader = findViewById(R.id.radio_group)
        radioImageLoader.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            seekBar.progress = 0
            when (checkedId) {
                R.id.radio_asset -> {
                    progressImageView.setImageResource(R.drawable.sidney)
                }

                R.id.radio_glide -> {
                    Glide.with(this@ProgressIndicatorDemoActivity)
                        .load("https://upload.wikimedia.org/wikipedia/commons/f/f5/Western_BACE_Cobblebank.jpg")
                        .into(progressImageView)
                }

                R.id.radio_picasso -> {
                    Picasso.get()
                        .load("https://upload.wikimedia.org/wikipedia/commons/f/f5/Western_BACE_Cobblebank.jpg")
                        .into(progressImageView)
                }
            }
        }
        var checkId = R.id.radio_asset
        if (savedInstanceState != null) {
            checkId = savedInstanceState.getInt(KEY_CHECKED, R.id.radio_asset)
            optionId = savedInstanceState.getInt(KEY_ITEM_ID, -1)
        }
        radioImageLoader.check(checkId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CHECKED, radioImageLoader.checkedRadioButtonId)
        outState.putInt(KEY_ITEM_ID, optionId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        if (optionId == -1) {
            return true
        }
        selectOption(optionId)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        reset()
        optionId = item.itemId
        item.isChecked = !item.isChecked
        val optionSelected = selectOption(optionId)
        return if (!optionSelected) {
            super.onOptionsItemSelected(item)
        } else true
    }

    private fun selectOption(@IdRes itemId: Int): Boolean {
        when (itemId) {
            R.id.action_indicator_blur -> {
                imageProgress.indicator = BlurIndicator(this)
                return true
            }

            R.id.action_indicator_colorfill -> {
                imageProgress.indicator =
                    ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT)
                return true
            }

            R.id.action_indicator_random_block -> {
                imageProgress.indicator = RandomBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL)
                return true
            }

            R.id.action_indicator_pixelize -> {
                imageProgress.indicator = PixelizeIndicator()
                return true
            }

            R.id.action_indicator_ciculator -> {
                imageProgress.indicator = CircularIndicator()
                return true
            }

            R.id.action_indicator_alpha -> {
                imageProgress.indicator = ColorizeIndicator()
                return true
            }

            R.id.action_indicator_stripe -> {
                imageProgress.indicator =
                    RandomStripeIndicator(RandomStripeIndicator.LEVEL_THIN)
                return true
            }

            R.id.action_indicator_spiral -> {
                imageProgress.indicator = SpiralIndicator()
                return true
            }

            R.id.action_indicator_diagonal -> {
                imageProgress.indicator = DiagonalIndicator()
                return true
            }

            R.id.action_indicator_snake -> {
                imageProgress.indicator = SnakeIndicator();
                return true
            }

            R.id.action_indicator_blocked_spiral -> {
                imageProgress.indicator = SpiralBlockIndicator()
                return true
            }

            else -> return false
        }
    }

    private fun reset() {
        seekBar.progress = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        reset()
        //        Picasso.get().shutdown();
        Glide.get(this).clearMemory()
    }

    companion object {
        private const val KEY_CHECKED = "checked"
        private const val KEY_ITEM_ID = "item_id"
    }
}
