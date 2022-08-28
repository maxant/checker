package ch.maxant.checker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartMyActivityAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // MyWorker.makeStatusNotification("Receiver with intent " + intent.getAction() + " " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), context);

        // Log.i("MAXANT", "YYY StartMyActivityAtBootReceiver#onReceive with " + intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // TODO doesn't seem to start the service:
            // Intent subIntent = new Intent(context, MainService.class);
            // subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startService(subIntent);
            // TODO use this instead?
            Intent subIntent = new Intent(context, MainActivity.class);
            subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(subIntent);
        }
    }
}