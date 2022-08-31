package ch.maxant.checker;

import android.content.Context;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ch.maxant.checker.MyWorker.makeStatusNotification;

public class EnsureWorkerIsRunning extends Worker {
    public EnsureWorkerIsRunning(@NotNull Context appContext, @NotNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NotNull
    @Override
    public Result doWork() {

        makeStatusNotification("PERIODIC: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), getApplicationContext());

        // this runs every 15m and ensure that work is running, even if the app is in the background
        MainActivity.WorkHelper.INSTANCE.addWork(); // ensure it runs again. we don't use periodic work, as that can only run every 15m which is crap for testing
        return Result.success();
    }
}
