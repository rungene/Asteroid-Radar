package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//we use this class to hold our Network Layer
//the api that ViewModel uses to communicate with our Webservice

private const val BASE_URL = "https://api.nasa.gov/"

//use the Moshi Builder to create a Moshi object with the KotlinJsonAdapterFactory:
/*private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()*/


/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object pointing to the desired URL
 */

private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()



/**
 * A public interface that exposes the [getProperties] method
 */

interface NasaApiService {
    /**
     * Returns a Retrofit callback that delivers a String
     * The @GET annotation indicates that the "neo/rest/v1/feed" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("neo/rest/v1/feed")
    suspend  fun getAsteroidsAsync(@Query("api_key") key:String, @Query("start_date") startDate:String, @Query("end_date") endDate:String): String
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object NasaApi {
    val retrofitService : NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}