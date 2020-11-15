package eu.bakici.imageprogressbar.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import java.util.LinkedList;

import eu.bakici.imageprogressbar.ProgressImageView;
import eu.bakici.imageprogressbar.indicator.AlphaIndicator;
import eu.bakici.imageprogressbar.indicator.BlockIndicator;
import eu.bakici.imageprogressbar.indicator.BlurIndicator;
import eu.bakici.imageprogressbar.indicator.CircularIndicator;
import eu.bakici.imageprogressbar.indicator.ColorFillIndicator;
import eu.bakici.imageprogressbar.indicator.PathIndicator;
import eu.bakici.imageprogressbar.indicator.PixelizeIndicator;
import eu.bakici.imageprogressbar.indicator.RandomBlockIndicator;
import eu.bakici.imageprogressbar.indicator.SpiralBlockIndicator;
import eu.bakici.imageprogressbar.indicator.WaveIndicator;


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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        reset();
        item.setChecked(!item.isChecked());

        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_indicator_blur:

                mProgressImageView.setProgressIndicator(new BlurIndicator(this));
                break;
            case R.id.action_indicator_colorfill:

                mProgressImageView.setProgressIndicator(new ColorFillIndicator(ColorFillIndicator.PROGRESS_DIRECTION_VERTICAL_TOP_DOWN));
                break;
            case R.id.action_indicator_random_block:

                mProgressImageView.setProgressIndicator(new RandomBlockIndicator(BlockIndicator.BLOCK_SIZE_SMALL));
                break;
            case R.id.action_indicator_spiral:

                mProgressImageView.setProgressIndicator(new SpiralBlockIndicator());
                break;
            case R.id.action_indicator_pixelize:

                mProgressImageView.setProgressIndicator(new PixelizeIndicator(this));
                break;
            case R.id.action_indicator_ciculator:
                mProgressImageView.setProgressIndicator(new CircularIndicator());
                break;
            case R.id.action_indicator_path:

                mProgressImageView.setProgressIndicator(new PathIndicator(new LinkedList<Point>()));
                break;
            case R.id.action_indicator_alpha:
                item.setChecked(!item.isChecked());
                mProgressImageView.setProgressIndicator(new AlphaIndicator());
                break;
            case R.id.action_indicator_wave:
                mProgressImageView.setProgressIndicator(new WaveIndicator(this));
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

