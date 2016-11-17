package eu.bakici.imageprogressbar.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    private ProgressImageView mProgressImageView;

    private SeekBar mSeeker;

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

        mProgressImageView = (ProgressImageView) findViewById(R.id.image);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        reset();
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_indicator_blur:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mProgressImageView.setProgressIndicator(new BlurIndicator(this));
                break;
            case R.id.action_indicator_colorfill:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mProgressImageView.setProgressIndicator(new ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_VERTICAL_TOP_DOWN));
                break;
            case R.id.action_indicator_random_block:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mProgressImageView.setProgressIndicator(new RandomBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL));
                break;
            case R.id.action_indicator_pixelize:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mProgressImageView.setProgressIndicator(new PixelizeIndicator(this));
                break;
            case R.id.action_indicator_ciculator:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mProgressImageView.setProgressIndicator(new CircularIndicator());
                break;
            case R.id.action_indicator_alpha:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mProgressImageView.setProgressIndicator(new AlphaIndicator());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void reset() {
        mProgressImageView.destroy();
        mSeeker.setProgress(0);
    }
}

