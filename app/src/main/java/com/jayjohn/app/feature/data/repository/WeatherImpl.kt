package com.jayjohn.app.feature.data.repository

import com.jayjohn.app.feature.data.datasource.remote.WeatherApi
import com.jayjohn.app.feature.domain.model.LocationData
import com.jayjohn.app.feature.domain.model.WeatherData
import com.jayjohn.app.feature.domain.repository.WeatherRepository

class WeatherImpl(
    private val weatherApi: WeatherApi
): WeatherRepository {

    override suspend fun getForecastFromCurrentLocation(keyword: String): WeatherData =
        weatherApi.getForecast(keyword = keyword)

    override suspend fun getLocation(keyword: String): List<LocationData> =
        weatherApi.getLocation(keyword = keyword)
}