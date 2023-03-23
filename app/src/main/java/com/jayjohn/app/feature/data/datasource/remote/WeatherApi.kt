package com.jayjohn.app.feature.data.datasource.remote

import com.jayjohn.app.feature.domain.model.LocationData
import com.jayjohn.app.feature.domain.model.WeatherData
import com.jayjohn.app.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String = Constants.API_KEY,
        @Query("q") keyword: String,
        @Query("days") numberOfDays: Int = Constants.NUMBER_OF_FORECAST_DAYS,
        @Query("aqi") airQuality: Boolean = true
    ): WeatherData

    @GET("search.json")
    suspend fun getLocation(
        @Query("key") apiKey: String = Constants.API_KEY,
        @Query("q") keyword: String,
    ): List<LocationData>
}