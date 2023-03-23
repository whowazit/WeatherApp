package com.jayjohn.app.feature.domain.repository

import com.jayjohn.app.feature.domain.model.LocationData
import com.jayjohn.app.feature.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getForecastFromCurrentLocation(
        keyword: String
    ): WeatherData

    suspend fun getLocation(
        keyword: String
    ): List<LocationData>
}