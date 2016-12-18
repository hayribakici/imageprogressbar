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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import eu.bakici.imageprogressbar.ProgressImageView;
import eu.bakici.imageprogressbar.indicator.AlphaIndicator;
import eu.bakici.imageprogressbar.indicator.BlockIndicator;
import eu.bakici.imageprogressbar.indicator.BlurIndicator;
import eu.bakici.imageprogressbar.indicator.CircularIndicator;
import eu.bakici.imageprogressbar.indicator.ColorFillIndicator;
import eu.bakici.imageprogressbar.indicator.PixelizeIndicator;
import eu.bakici.imageprogressbar.indicator.RandomBlockIndicator;


public class HelloAndroidActivity extends Activity {

    public static final String TAG = "ImageProgress";

    private ProgressImageView progressImageView;

    private SeekBar seeker;


    private boolean stopSmoother = false;
    private SmoothProgressRunnable smoothProgressRunnable = new SmoothProgressRunnable(0);

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressImageView = (ProgressImageView) findViewById(R.id.image);
        progressImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.sidney));
        seeker = (SeekBar) findViewById(R.id.progress_bar);
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                Log.d(TAG, "position == " + progress);
                progressImageView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stopSmoother) {
                    v.post(smoothProgressRunnable);
                    stopSmoother = true;
                } else {
                    v.removeCallbacks(smoothProgressRunnable);
                    stopSmoother = false;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);
        reset();
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_indicator_blur:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                progressImageView.setProgressIndicator(new BlurIndicator(this));
                return true;
            case R.id.action_indicator_colorfill:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                progressImageView.setProgressIndicator(new ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT));
                return true;
            case R.id.action_indicator_random_block:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                progressImageView.setProgressIndicator(new RandomBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL));
                return true;
            case R.id.action_indicator_pixelize:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                progressImageView.setProgressIndicator(new PixelizeIndicator(this));
                return true;
            case R.id.action_indicator_ciculator:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                progressImageView.setProgressIndicator(new CircularIndicator());
                return true;
            case R.id.action_indicator_alpha:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                progressImageView.setProgressIndicator(new AlphaIndicator());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void reset() {
        progressImageView.destroy();
        seeker.setProgress(0);
        smoothProgressRunnable.setProgress(0);
    }

    private class SmoothProgressRunnable implements Runnable {

        private int progress;

        public SmoothProgressRunnable(int progress) {
            this.progress = progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        @Override
        public void run() {
            if (progressImageView == null) {
                return;
            }
            seeker.setProgress(++progress);
            if (progress >= 100) {
                return;
            }
            progressImageView.postDelayed(this, 200);
        }
    }
}

