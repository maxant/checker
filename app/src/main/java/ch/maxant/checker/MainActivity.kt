package ch.maxant.checker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import ch.maxant.checker.WorkHelper.enqueueSiteCheckerWorker
import ch.maxant.checker.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.time.Duration


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // https://developer.android.com/topic/libraries/architecture/workmanager
        // -> https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work#schedule_periodic_work
//        val periodicWork = PeriodicWorkRequest.Builder(
//            EnsureWorkerIsRunning::class.java,
//            15, TimeUnit.MINUTES // repeatInterval (the period cycle)
//            // , 1, TimeUnit.MINUTES // flexInterval
//        )
//            .build()
//        val workManager = WorkManager.getInstance()
//        workManager.enqueueUniquePeriodicWork("MainActivityPeriodicWorker", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
    }

    override fun onStart() {
        super.onStart()

        /*
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val defaultValue = resources.getString(R.string.first_start_value)
        if(defaultValue == not-set) {
        with (sharedPref.edit()) {
            putString(getString(R.string.first_start_value), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            apply()
        }
        */
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_refresh -> {
                enqueueSiteCheckerWorker(Duration.ofSeconds(0))
                Model.lastNotificationOKUpdater = {} // the user has clearly clicked the refresh button, they are in the app, they don't need a check mark, on the notification
                return true
            }
            R.id.action_settings -> {
                Toast.makeText(applicationContext, "todo", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}