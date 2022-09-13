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
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static ch.maxant.checker.WorkHelper.enqueueSiteCheckerWorker;

public class SiteCheckerWorker extends Worker {

    public static final String TAG = "MAXANT";

    public SiteCheckerWorker(@NotNull Context appContext, @NotNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NotNull
    @Override
    public Result doWork() {

        Query query = Controller.getLatestWaitingToStartQueryAndMarkAsStarted();

        // TODO list of sites
        String urlString = "https://abstratium.dev/hostagent/rc";
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
                        response.append(inputLine).append("\r\n");
                    }
                    Log.d(TAG, "YYY call to site " + urlString + " successful"); // + response);
                    Controller.updateQuerySuccess(query, "OK");
                    parseResponse(response.toString());
                }
            } else {
                Log.w(TAG, "YYY site " + urlString + " failed with code: " + responseCode);
                Notifications.notify("FAILED " + responseCode, getApplicationContext());
                Controller.updateQueryFailed(query, "Code " + responseCode);
            }
        } catch (Exception e) {
            Log.w(TAG, "YYY site " + urlString + " failed with exception: " + e.getMessage(), e);
            Notifications.notify("FAILED " + e.getMessage(), getApplicationContext());
            Controller.updateQueryFailed(query, e.getMessage());
        }

        // ensure it runs again.
        // we don't use periodic work for these individual checks, as that can only run every 15m which is crap for testing
        enqueueSiteCheckerWorker(Duration.ofMinutes(5));

        // Map<String, Object> map = new HashMap<>();
        // map.put("asdf", "asdf");
        return Result.success(/*new Data.Builder().putAll(map).build()*/);
    }

    private void parseResponse(String response) {
        // 2022-09-13T00:09:30.19542901<br><br>Return code: 0<br><br>
        // ...stdout, see below...
        //<br><br>Saving debug log to /var/log/letsencrypt/letsencrypt.log

        StringTokenizer st = new StringTokenizer(response.replace("<br>", ">"), ">");
        if(st.hasMoreTokens()) {
            String date = st.nextToken();
            if(st.hasMoreTokens()) {
                String returnCode = st.nextToken().trim();
                if(!"Return code: 0".equals(returnCode)) {
                    Controller.certificatesChanged("unexpected return code: \r\n\r\n'" + returnCode + "'");
                    Notifications.notify("CERTS ERROR", getApplicationContext());
                } else if(st.hasMoreTokens()) {
                    String stdout = st.nextToken().trim();
                    List<Certificate> certificates = parseCertificates(stdout);

                    if(certificates.stream().anyMatch(c -> c.getWarnings() != null)) {
                        Notifications.notify("CERTS WARN FOR DOMAIN", getApplicationContext());
                    }
                    if(certificates.stream().anyMatch(c -> LocalDate.now().plusDays(20).isAfter(c.getExpiry()))) {
                        Notifications.notify("CERTS EXPIRY", getApplicationContext());
                    }

                    if(st.hasMoreTokens()) {
                        String stderr = st.nextToken().trim();
                        if(stderr.length() > "Saving debug log to /var/log/letsencrypt/letsencrypt.log".length()) {
                            Controller.certificatesChanged(date, certificates, stderr.substring(56));
                            Notifications.notify("CERTS WARN", getApplicationContext());
                        } else {
                            Controller.certificatesChanged(date, certificates);
                        }
                    }
                } else {
                    Controller.certificatesChanged("unexpected response structure with no stdout: " + response);
                    Notifications.notify("CERTS ERROR", getApplicationContext());
                }
            } else {
                Controller.certificatesChanged("unexpected response structure with no return code: " + response);
                Notifications.notify("CERTS ERROR", getApplicationContext());
            }
        } else {
            Controller.certificatesChanged("unexpected response structure with no date: " + response);
            Notifications.notify("CERTS ERROR", getApplicationContext());
        }
    }

    private List<Certificate> parseCertificates(String stdout) {
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        //Found the following certs:
        //  Certificate Name: abstratium.dev
        //    Domains: abstratium.dev
        //    Expiry Date: 2022-09-24 18:14:09+00:00 (VALID: 11 days)
        //    Certificate Path: /etc/letsencrypt/live/abstratium.dev/fullchain.pem
        //    Private Key Path: /etc/letsencrypt/live/abstratium.dev/privkey.pem
        //  ...
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        List<Certificate> certificates = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(stdout.replace('\r', '\n'), "\n");
        while(st.hasMoreTokens()) {
            String line = st.nextToken().trim();
            if(line.startsWith("-") || line.startsWith("Found the following certs")) {
            } else if(line.startsWith("Certificate Name")) {
                String domain = st.nextToken().trim();
                domain = domain.substring("Domains: ".length()).trim();
                String expiryLine = st.nextToken().trim();
                String[] expiries = expiryLine.split("\\(");
                String expiry = expiries[0].trim();
                expiry = expiry.substring("Expiry Date: ".length()).trim();
                expiry = expiry.substring(0, 10);
                String validity = expiries[1].trim();
                validity = validity.replace('(', ' ');
                validity = validity.replace(')', ' ').trim();
                validity = validity.substring("VALID: ".length());
                String paths = st.nextToken() + "::" + st.nextToken();
                certificates.add(new Certificate(domain, LocalDate.parse(expiry), validity, paths.contains("00") ? paths : null));
            } else {
                certificates.add(new Certificate("unknown", LocalDate.parse("1970-01-01"), "unknown", "unexpected line " + line));
            }
        }
        return certificates;
    }

}
