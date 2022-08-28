package ch.maxant.checker

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ch.maxant.checker.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit


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

        WorkHelper.addWork()
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    object WorkHelper {
        fun addWork() {
            // unable to start on install -> so it starts on reboot, and on app start
            // https://stackoverflow.com/questions/2127044/how-to-start-android-service-on-installation
            // https://stackoverflow.com/questions/4467600/how-to-launch-a-android-service-when-the-app-launches
            // val serviceIntent = Intent(applicationContext, MainService::class.java)
            // startService(serviceIntent)

            // https://developer.android.com/topic/libraries/architecture/workmanager
            // -> https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work#schedule_periodic_work
            val work = OneTimeWorkRequest.Builder(
                MyWorker::class.java
            )
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()
            val workManager = WorkManager.getInstance()
            val op = workManager.enqueueUniqueWork("MainActivityWorker", ExistingWorkPolicy.REPLACE, work)
            Log.i("MAXANT", "YYY workManager returned op " + op)
        }
    }
}