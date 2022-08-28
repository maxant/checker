package ch.maxant.checker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * https://developer.android.com/guide/components/services
 */
public class MainService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("MAXANT", "YYY MainService#onBind " + intent.getAction());
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        Log.i("MAXANT", "YYY MainService is created");

        // https://developer.android.com/topic/libraries/architecture/workmanager
        // -> https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work#schedule_periodic_work
        PeriodicWorkRequest work =
                new PeriodicWorkRequest.Builder(MyWorker.class,
                        1, TimeUnit.MINUTES // repeatInterval (the period cycle)
                        , 1, TimeUnit.MINUTES // flexInterval
                )
                .build();

        WorkManager workManager = WorkManager.getInstance();
        // workManager.enqueueUniquePeriodicWork("MainServiceWorker", ExistingPeriodicWorkPolicy.REPLACE, work);
    }
}
