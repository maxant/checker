package ch.maxant.checker;

import android.content.Context;
import android.util.Log;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static ch.maxant.checker.MainActivity.WorkHelper.addSiteCheckerWorker;

public class SiteCheckerWorker extends Worker {

    public static final String TAG = "MAXANT";

    public SiteCheckerWorker(@NotNull Context appContext, @NotNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NotNull
    @Override
    public Result doWork() {

        Query query = Model.addQuery();

        // TODO list of sites
        String urlString = "https://www.maxant.ch";
        try {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) { // success
                try(BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    Log.d(TAG, "YYY call to site " + urlString + " successful: " + response);
                    // TODO parse response according to requirements per site
                    query.setSuccess("OK");
                }
            } else {
                Log.w(TAG, "YYY site " + urlString + " failed with code: " + responseCode);
                Notifications.notify("FAILED " + responseCode, getApplicationContext());
                query.setFailed("Code " + responseCode);
            }
        } catch (Exception e) {
            Log.w(TAG, "YYY site " + urlString + " failed with exception: " + e.getMessage(), e);
            Notifications.notify("FAILED " + e.getMessage(), getApplicationContext());
            query.setFailed(e.getMessage());
        }

        // ensure it runs again.
        // we don't use periodic work for these individual checks, as that can only run every 15m which is crap for testing
        addSiteCheckerWorker();

        // Map<String, Object> map = new HashMap<>();
        // map.put("asdf", "asdf");
        return Result.success(/*new Data.Builder().putAll(map).build()*/);
    }

}
