package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import androidx.work.WorkManager
import com.udacity.asteroidradar.work.AsteroidWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class App :Application() {

//Create a coroutine scope to use for your application:
    val applicationScope = CoroutineScope(Dispatchers.Default)


  //  Create an initialization function that does not block the main thread:
    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

  /*  It's important to note that WorkManager.initialize should be called from inside
    onCreate without using a background thread to avoid issues caused when initialization happens
    after WorkManager is used.*/

    override fun onCreate() {

        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        delayedInit()
    }
   /* Create a setupRecurringWork() function. In it, define a repeatingRequest variable that uses a
    PeriodicWorkRequestBuilder to create a PeriodicWorkRequest for your RefreshDataWorker*/


    private fun setupRecurringWork() {

      /*  use a Builder to define constraints In setupRecurringWork(). Define constraints to
        prevent work from occurring when there is no network access or the device is low
        on battery.*/

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setRequiresDeviceIdle(true)
                    }
                }.build()

        //Add the constraints:
        val repeatingRequest = PeriodicWorkRequestBuilder<AsteroidWork>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

// Schedule the work as unique:
        WorkManager.getInstance().enqueueUniquePeriodicWork(
                AsteroidWork.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest)
    }
}