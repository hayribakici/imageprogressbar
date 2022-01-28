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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import eu.bakici.imageprogressbar.ProgressImageView;
import eu.bakici.imageprogressbar.indicator.BlockIndicator;
import eu.bakici.imageprogressbar.indicator.BlurIndicator;
import eu.bakici.imageprogressbar.indicator.CircularIndicator;
import eu.bakici.imageprogressbar.indicator.ColorFillIndicator;
import eu.bakici.imageprogressbar.indicator.ColorizeIndicator;
import eu.bakici.imageprogressbar.indicator.DiagonalIndicator;
import eu.bakici.imageprogressbar.indicator.PixelizeIndicator;
import eu.bakici.imageprogressbar.indicator.RandomBlockIndicator;
import eu.bakici.imageprogressbar.indicator.RandomStripeIndicator;
import eu.bakici.imageprogressbar.indicator.SpiralIndicator;


public class ProgressIndicatorDemoActivity extends Activity {

    private static final String KEY_CHECKED = "checked";
    private static final String KEY_ITEM_ID = "item_id";

    private ProgressImageView progressImageView;
    private SeekBar seekBar;
    private RadioGroup radioImageLoader;
    private Button startButton;
    private int optionId = -1;
    private boolean state = false;
    Runnable autoProgressRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = seekBar.getProgress();
            if (progress == seekBar.getMax()) {
                startButton.setText(R.string.start);
                state = false;
                return;
            }

            seekBar.setProgress(progress + 1);
            if (state) {
                seekBar.postDelayed(this, 300);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressImageView = findViewById(R.id.image);
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
        startButton = findViewById(R.id.button);
        startButton.setOnClickListener((view) -> {
            state = !state;
            if (state) {
                ((Button) view).setText(R.string.stop);
            } else {
                ((Button) view).setText(R.string.start);
            }
            view.post(autoProgressRunnable);
        });
        radioImageLoader = findViewById(R.id.radio_group);
        radioImageLoader.setOnCheckedChangeListener((group, checkedId) -> {
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
        });
        int checkId = R.id.radio_asset;
        if (savedInstanceState != null) {
            checkId = savedInstanceState.getInt(KEY_CHECKED, R.id.radio_asset);
            optionId = savedInstanceState.getInt(KEY_ITEM_ID, -1);
        }
        radioImageLoader.check(checkId);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CHECKED, radioImageLoader.getCheckedRadioButtonId());
        outState.putInt(KEY_ITEM_ID, optionId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (optionId == -1) {
            return true;
        }
        selectOption(optionId);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);
        reset();
        optionId = item.getItemId();
        item.setChecked(!item.isChecked());
        boolean optionSelected = selectOption(optionId);
        if (!optionSelected) {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private boolean selectOption(@IdRes final int itemId) {
        if (itemId == R.id.action_indicator_blur) {
            progressImageView.setProgressIndicator(new BlurIndicator(this));
            return true;
        } else if (itemId == R.id.action_indicator_colorfill) {
            progressImageView.setProgressIndicator(new ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_HORIZONTAL_LEFT_RIGHT));
            return true;
        } else if (itemId == R.id.action_indicator_random_block) {
            progressImageView.setProgressIndicator(new RandomBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL));
            return true;
        } else if (itemId == R.id.action_indicator_pixelize) {
            progressImageView.setProgressIndicator(new PixelizeIndicator());
            return true;
        } else if (itemId == R.id.action_indicator_ciculator) {
            progressImageView.setProgressIndicator(new CircularIndicator());
            return true;
        } else if (itemId == R.id.action_indicator_alpha) {
            progressImageView.setProgressIndicator(new ColorizeIndicator());
            return true;
        } else if (itemId == R.id.action_indicator_stripe) {
            progressImageView.setProgressIndicator(new RandomStripeIndicator(RandomStripeIndicator.LEVEL_THIN));
            return true;
        } else if (itemId == R.id.action_indicator_spiral) {
            progressImageView.setProgressIndicator(new SpiralIndicator());
            return true;
        } else if (itemId == R.id.action_indicator_diagonal) {
            progressImageView.setProgressIndicator(new DiagonalIndicator());
            return true;
        }
        return false;
    }

    private void reset() {
        seekBar.setProgress(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reset();
//        Picasso.get().shutdown();
        Glide.get(this).clearMemory();
    }
}

