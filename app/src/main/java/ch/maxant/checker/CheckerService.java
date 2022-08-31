package ch.maxant.checker;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static ch.maxant.checker.MyWorker.*;

/**
 * https://developer.android.com/guide/components/services
 */
public class CheckerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("MAXANT", "YYY MainService#onBind " + intent.getAction());
        return null;
    }

    @Override
    public void onCreate() {

        Log.i("MAXANT", "YYY Service is created 1");
        super.onCreate();
        Log.i("MAXANT", "YYY Service is created 2");

        Model.INSTANCE.setCreatedAt(LocalDateTime.now());
        Log.i("MAXANT", "YYY Service is created 3");

        MainActivity.WorkHelper.INSTANCE.addWork();
        Log.i("MAXANT", "YYY Service is created 4");

        // https://developer.android.com/topic/libraries/architecture/workmanager
        // -> https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work#schedule_periodic_work
        PeriodicWorkRequest work =
                new PeriodicWorkRequest.Builder(EnsureWorkerIsRunning.class,
                        15, TimeUnit.MINUTES // repeatInterval (the period cycle)
                        // , 1, TimeUnit.MINUTES // flexInterval
                )
                .build();

        WorkManager workManager = WorkManager.getInstance();
        workManager.enqueueUniquePeriodicWork("CheckerServiceWorker", ExistingPeriodicWorkPolicy.REPLACE, work);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Checker Info")
                .setContentText("checker service is running")
                // TODO .addAction(new NotificationCompat.Action(null, "myAction", PendingIntent.))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .build();
        startForeground(NOTIFICATION_ID, notification);

        return Service.START_STICKY; // persistent: https://www.brightdevelopers.com/android-tips-activity-service/
    }
}
