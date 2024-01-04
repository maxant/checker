package ch.maxant.checker;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static ch.maxant.checker.Notifications.CHANNEL_ID;
import static ch.maxant.checker.Notifications.NOTIFICATION_ID;
import static ch.maxant.checker.SiteCheckerWorker.TAG;
import static ch.maxant.checker.WorkHelper.enqueuePeriodic_EnsureWorkerIsRunning;

/**
 * https://developer.android.com/guide/components/services
 */
public class CheckerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // don't bind
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // something like the following records the current time, each time
        // the application is instantiated and displayed on the screen
        // Model.INSTANCE.setCreatedAt(LocalDateTime.now());

        Log.d(TAG, "YYY CheckerService is created. enqueuing periodic worker to ensure the site checker runs regularly...");

        enqueuePeriodic_EnsureWorkerIsRunning();
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
