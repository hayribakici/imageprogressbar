package eu.bakici.imageprogressbar;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.bakici.imageprogressbar.indicator.ProgressIndicator;

final class ProgressExecutor {


    @NonNull
    private final Bitmap originalBitmap;
    @NonNull
    private final OnPostExecuteListener<Bitmap> listener;
    @NonNull
    private final ExecutorService executor;
    @NonNull
    private final Handler mainThreadHandler;
    @NonNull
    ProgressIndicator indicator;

    public ProgressExecutor(@NonNull Bitmap originalBitmap,
                            @NonNull ProgressIndicator indicator,
                            @NonNull OnPostExecuteListener<Bitmap> listener) {
        this.originalBitmap = originalBitmap;
        this.listener = listener;
        this.executor = Executors.newSingleThreadExecutor();
        this.indicator = indicator;
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    void prepare() {
        start(true, 0f);
    }

    void start(int progress) {
        start(false, (float) progress);
    }

    void start(float progress) {
        start(false, progress);
    }


    private void start(final boolean isPreProgress, float progress) {
        executor.execute(() -> {
            if (isPreProgress) {
                indicator.onPreProgress(originalBitmap);
            } else {
                if (indicator.getIndicationProcessingType() == ProgressIndicator.HYBRID) {

                } else {
                    indicator.onProgress(originalBitmap, (int) progress);
                }
            }
            // post back to main thread
            mainThreadHandler.post(() -> listener.onPostExecute(indicator.getCurrentBitmap()));
        });
    }

}
