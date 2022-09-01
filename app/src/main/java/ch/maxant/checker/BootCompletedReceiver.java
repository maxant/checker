package ch.maxant.checker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static ch.maxant.checker.SiteCheckerWorker.TAG;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // unable to start on install -> so it only starts on reboot and app start
        // https://stackoverflow.com/questions/2127044/how-to-start-android-service-on-installation
        // https://stackoverflow.com/questions/4467600/how-to-launch-a-android-service-when-the-app-launches

        // https://stackoverflow.com/questions/43913937/intent-boot-completed-not-working-on-huawei-device
        // settings -> Battery -> App launch -> Disable automatic management for your app

        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "YYY StartMyActivityAtBootReceiver#onReceive with action " + intent.getAction());
            Intent subIntent = new Intent(context, CheckerService.class);
            context.startForegroundService(subIntent);

            // alternatively, start the app like this:
            // Intent subIntent = new Intent(context, MainActivity.class);
            // subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(subIntent);
        }
    }
}