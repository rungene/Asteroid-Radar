package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.Image

import com.udacity.asteroidradar.api.ImageApi
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception


/*
The MVVM pattern is a pattern that helps to completely separate the business and presentation
logic from the UI, this makes your app easier for testing and for maintenance.

The View is responsible for the layout structured displayed on the screen.

The ViewModel implements the data and commands connected to the View to notify the View of
state changes via change notifications events.

The Model is a non-visual class that has the data to use.

The ViewModel knows Model but does not know View and View can know ViewModel but does not know Model.*/



/**
 * The [ViewModel] that is attached to the [MainFragment].
 */

enum class ImageApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid


    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<ImageApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<ImageApiStatus>
        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the NasaProperty with
    // new values
    private val _image = MutableLiveData<Image>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val image: LiveData<Image>
        get() = _image


    private val database= getDatabase(application)
    private val asteroidRepository= AsteroidRepository(database)
    val asteroids= asteroidRepository.asteroids

    /**
     * Call    getImageOfTheDay() on init so we can display status immediately.
     */
    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }

       // Timber.d("Inside init block")
        Log.d("MainViewModel","Inside init block")

        getImageOfTheDay()
    }

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    /**
     * Sets the value of the status LiveData to the NASA API status.
     */

    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
     */
/*
    private fun getNasaAsteroids() {
        viewModelScope.launch {

            try {
                var listResult = NasaApi.retrofitService.getProperties()
                _status.value = "Success: ${listResult.size} Nasa properties retrieved"
                if (listResult.size > 0) {
                    _image.value = listResult[0]
                }

            }catch (e: Exception){
                _status.value = "Failure: ${e.message}"

            }

        }

    }*/

    private fun getImageOfTheDay() {
        viewModelScope.launch {

            _status.value = ImageApiStatus.LOADING
            try {
                var result = ImageApi.retrofitService.getImageOfTheDay(Constants.API_KEY)
                _image.value=result
                _status.value = ImageApiStatus.DONE
            } catch (e: Exception) {
                _status.value=ImageApiStatus.ERROR
                _image.value=Image("","","","","","","","")

            }
        }
        }

    }

