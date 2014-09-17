package com.thehayro.view;

import com.thehayro.view.indicator.BlockIndicator;
import com.thehayro.view.indicator.CircularIndicator;
import com.thehayro.view.indicator.ColorFillIndicator;
import com.thehayro.view.indicator.BlurIndicator;
import com.thehayro.view.indicator.PixelizeIndicator;
import com.thehayro.view.indicator.ProgressIndicator;
import com.thehayro.view.indicator.RandomBlockIndicator;
import com.thehayro.view.indicator.SpiralBlockIndicator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class HelloAndroidActivity extends Activity {

    public static final String TAG = "ImageProgress";

    private ProgressImageView mProgressImageView;

    private SeekBar mSeeker;
    private ProgressBar mProgressBar;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressImageView  = (ProgressImageView) findViewById(R.id.image);
        mSeeker = (SeekBar) findViewById(R.id.progress_bar);
        mSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                Log.d(TAG, "position == " + progress);
                mProgressImageView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(com.thehayro.view.R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        reset();
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_indicator_blur:
                mProgressImageView.setProgressIndicator(new BlurIndicator(this));
                break;
            case R.id.action_indicator_colorfill:
                mProgressImageView.setProgressIndicator(new ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_VERTICAL_TOP_DOWN));
                break;
            case R.id.action_indicator_random_block:
                mProgressImageView.setProgressIndicator(new RandomBlockIndicator(BlockIndicator.BlockSize.EXTRA_SMALL));
                break;
            case R.id.action_indicator_spiral:
                mProgressImageView.setProgressIndicator(new SpiralBlockIndicator());
                break;
            case R.id.action_indicator_pixelize:
                mProgressImageView.setProgressIndicator(new PixelizeIndicator(this));
                break;
            case R.id.action_indicator_ciculator:
                mProgressImageView.setProgressIndicator(new CircularIndicator(ProgressIndicator.IndicationProcessingType.SYNC));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reset() {
        mProgressImageView.destroy();
        mSeeker.setProgress(0);
    }
}

