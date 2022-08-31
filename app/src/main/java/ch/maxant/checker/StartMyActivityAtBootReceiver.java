package ch.maxant.checker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartMyActivityAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // MyWorker.makeStatusNotification("Receiver with intent " + intent.getAction() + " " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), context);
// https://stackoverflow.com/questions/43913937/intent-boot-completed-not-working-on-huawei-device
        // settings -> Battery -> App launch -> Disable automatic management for your app
        Log.i("MAXANT", "YYY StartMyActivityAtBootReceiver#onReceive");
        Log.i("MAXANT", "YYY StartMyActivityAtBootReceiver#onReceive with " + (intent != null ? intent.getAction() : intent));
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Toast.makeText(context, "started checker", Toast.LENGTH_SHORT).show();
            Intent subIntent = new Intent(context, CheckerService.class);
            context.startForegroundService(subIntent);
            // TODO use this instead?
            // Intent subIntent = new Intent(context, MainActivity.class);
            // subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(subIntent);
        }
    }
}