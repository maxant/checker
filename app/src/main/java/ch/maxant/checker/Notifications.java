package ch.maxant.checker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ch.maxant.checker.SiteCheckerWorker.TAG;

public class Notifications {
    // Name of Notification Channel for verbose notifications of background work
    private static final String VERBOSE_NOTIFICATION_CHANNEL_NAME = "Checker Notifications";
    private static final String VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever there is a problem";
    public static final String CHANNEL_ID = "CHECKER_NOTIFICATION";
    public static final int NOTIFICATION_ID = 1;

    public static void notify(String message, Context context) {
        // needs to be on a thread which called looper.lopper: Toast.makeText(context, "started checker", Toast.LENGTH_SHORT).show();

        Log.i(TAG, "YYY makeStatusNotification: " + message);

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // -> logged 28 is more than 26
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            String name = VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // pause, vibrate, ... repeat
            /* TODO enable!
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{200L, 2000L, 200L, 2000L, 200L, 2000L,  // - - - S
                                                   200L,  800L, 200L,  800L, 200L,  800L,  // . . . O
                                                   200L, 2000L, 200L, 2000L, 200L, 2000L,  // - - - S
                                                    });
             */

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Checker Problem " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(new long[0])

                .setContentIntent(contentIntent) // on click, open app
                .setAutoCancel(true) // remove on click after opening app

                .build();

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification);
    }

}
