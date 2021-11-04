package eu.bakici.imageprogressbar.demo;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import eu.bakici.imageprogressbar.ProgressImageView;
import eu.bakici.imageprogressbar.indicator.AlphaIndicator;
import eu.bakici.imageprogressbar.indicator.BlockIndicator;
import eu.bakici.imageprogressbar.indicator.BlurIndicator;
import eu.bakici.imageprogressbar.indicator.CircularIndicator;
import eu.bakici.imageprogressbar.indicator.ColorFillIndicator;
import eu.bakici.imageprogressbar.indicator.PixelizeIndicator;
import eu.bakici.imageprogressbar.indicator.RandomBlockIndicator;
import eu.bakici.imageprogressbar.indicator.RandomStripeIndicator;
import eu.bakici.imageprogressbar.indicator.SpiralIndicator;


public class ProgressIndicatorDemoActivity extends Activity {

    public static final String TAG = "ImageProgress";

    private ProgressImageView progressImageView;
    private SeekBar seekBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressImageView = findViewById(R.id.image);
        progressImageView.setImageResource(R.drawable.sidney);
        seekBar = findViewById(R.id.progress_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                progressImageView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        });
        RadioGroup radioImageLoader = findViewById(R.id.radio_image_loader);
        radioImageLoader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                seekBar.setProgress(0);
                if (checkedId == R.id.radio_asset) {
                    progressImageView.setImageResource(R.drawable.sidney);
                } else if (checkedId == R.id.radio_glide) {
                    Glide.with(ProgressIndicatorDemoActivity.this)
                            .load("https://upload.wikimedia.org/wikipedia/commons/f/f5/Western_BACE_Cobblebank.jpg")
                            .into(progressImageView);
                } else if (checkedId == R.id.radio_picasso) {
                    Picasso.get()
                            .load("https://upload.wikimedia.org/wikipedia/commons/f/f5/Western_BACE_Cobblebank.jpg")
                            .into(progressImageView);
                }
            }
        });
        radioImageLoader.check(R.id.radio_asset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);
        reset();
        final int itemId = item.getItemId();
        item.setChecked(!item.isChecked());
        switch (itemId) {
            case R.id.action_indicator_blur:
                progressImageView.setProgressIndicator(new BlurIndicator(this));
                return true;
            case R.id.action_indicator_colorfill:
                progressImageView.setProgressIndicator(new ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT));
                return true;
            case R.id.action_indicator_random_block:
                progressImageView.setProgressIndicator(new RandomBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL));
                return true;
            case R.id.action_indicator_pixelize:
                progressImageView.setProgressIndicator(new PixelizeIndicator());
                return true;
            case R.id.action_indicator_ciculator:
                progressImageView.setProgressIndicator(new CircularIndicator());
                return true;
            case R.id.action_indicator_alpha:
                progressImageView.setProgressIndicator(new AlphaIndicator());
                return true;
            case R.id.action_indicator_stripe:
                progressImageView.setProgressIndicator(new RandomStripeIndicator(RandomStripeIndicator.LEVEL_THIN));
                return true;
            case R.id.action_indicator_spiral:
                progressImageView.setProgressIndicator(new SpiralIndicator());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void reset() {
        seekBar.setProgress(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reset();
        Picasso.get().shutdown();
        Glide.get(this).clearMemory();
    }
}

