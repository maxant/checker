package ch.maxant.checker

import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object WorkHelper {
    @JvmStatic
    fun enqueueSiteCheckerWorker(delay: Duration) {
        // https://developer.android.com/topic/libraries/architecture/workmanager
        val clazz = SiteCheckerWorker::class.java
        val work = OneTimeWorkRequest.Builder(clazz)
            .setInitialDelay(delay)
            .build()
        val workManager = WorkManager.getInstance()

        Controller.addQuery(LocalDateTime.now().plus(delay))

        workManager.enqueueUniqueWork(clazz.name, ExistingWorkPolicy.REPLACE, work)
    }

    @JvmStatic
    fun enqueuePeriodic_EnsureWorkerIsRunning() {
        // https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work#schedule_periodic_work
        val clazz = EnsureWorkerIsRunning::class.java
        val work = PeriodicWorkRequest.Builder(
                clazz,
                15, TimeUnit.MINUTES // repeatInterval (the period cycle)
                // , 1, TimeUnit.MINUTES // flexInterval
            )
            .build()

        val workManager = WorkManager.getInstance()
        workManager.enqueueUniquePeriodicWork(clazz.name, ExistingPeriodicWorkPolicy.REPLACE, work)

    }
}
