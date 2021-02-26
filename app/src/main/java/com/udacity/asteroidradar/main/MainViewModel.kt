package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.Image
import com.udacity.asteroidradar.api.ImageApi
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaProperty
import kotlinx.coroutines.launch
import java.lang.Exception


/**
 * The [ViewModel] that is attached to the [MainFragment].
 */

enum class ImageApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

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



    /**
     * Call    getImageOfTheDay() on init so we can display status immediately.
     */
    init {
        getImageOfTheDay()
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

