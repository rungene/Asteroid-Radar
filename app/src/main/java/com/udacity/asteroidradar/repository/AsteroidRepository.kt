package com.udacity.asteroidradar.repository

import android.util.Log
import android.util.TimeFormatException
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter


// Repository for fetching from network Asteroids to store them in local DB
class AsteroidRepository(private val database: AsteroidDatabase) {
    private val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()

    // Load the asteroids from offline cache
    // Need to convert from database Asteroid to domain Asteroid so it can be shown on the screen
    var asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAllAsteroids()){
        it.asDomainModel()
    }

    val asteroidsForWeek: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao
            .getNextSevenDaysAsteroidFromDB(nextSevenDaysFormattedDates[0], nextSevenDaysFormattedDates[6])) {
        it.asDomainModel()
    }
    // Refresh the database to make sure it's up-to-date
    suspend fun refreshAsteroids(){
        var asteroidArray= listOf<DatabaseAsteroid>()

        withContext(Dispatchers.IO){

            try {
                val json=NasaApi.retrofitService.getAsteroidsAsync(Constants.API_KEY,
                        nextSevenDaysFormattedDates[0],nextSevenDaysFormattedDates[6])
                val obj = JSONObject(json)
                val asteroidList= parseAsteroidsJsonResult(obj)
                Log.i("asteroidList",""+asteroidList.size)
                // tried printing asteroidlist[0]. this is printing crctly
                val databaseAsteroid= asteroidList.map {
                    DatabaseAsteroid(
                            id = it.id,
                            codename = it.codename,
                            closeApproachDate = it.closeApproachDate,
                            absoluteMagnitude = it.absoluteMagnitude,
                            estimatedDiameter=it.estimatedDiameter,
                            relativeVelocity = it.relativeVelocity,
                            distanceFromEarth = it.distanceFromEarth,
                            isPotentiallyHazardous = it.isPotentiallyHazardous
                    )
                }

                asteroidArray=databaseAsteroid
                var final=asteroidArray.toTypedArray()
                Log.e("closedate",asteroidArray[0].closeApproachDate)
                // this is printing null.
                database.asteroidDao.insertAll(*final)
                //successful
                Timber.d("Inside try block")
            } catch (t: Throwable) {
                val stacktrace = StringWriter().also { t.printStackTrace(PrintWriter(it)) }.toString().trim()
                //failed
               // Timber.d("error inside catch block:${t.printStackTrace()}")
                Log.e("Repository :",stacktrace)
            }

        }
    }
}
