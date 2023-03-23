package com.jayjohn.app.feature.domain.model

data class WeatherData(
    val location: Location,
    val current: Current,
    val forecast: Forecast,
)
