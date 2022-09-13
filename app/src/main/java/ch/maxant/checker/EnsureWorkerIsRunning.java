package ch.maxant.checker;

import android.content.Context;
import android.util.Log;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ch.maxant.checker.SiteCheckerWorker.TAG;
import static ch.maxant.checker.WorkHelper.enqueueSiteCheckerWorker;

public class EnsureWorkerIsRunning extends Worker {
    public EnsureWorkerIsRunning(@NotNull Context appContext, @NotNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NotNull
    @Override
    public Result doWork() {

        Log.i(TAG, "YYY EnsureWorkerIsRunning periodic worker now running: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // this code here runs every ~15m and ensures that work is running, even if the app is in the background

        // enqueue actual work. we don't use periodic work there, as that can only run every 15m which is crap for testing
        enqueueSiteCheckerWorker(Duration.ofSeconds(5)); // start pretty much immediately

        return Result.success();
    }
}
