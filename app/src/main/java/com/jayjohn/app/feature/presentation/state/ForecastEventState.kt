package com.jayjohn.app.feature.presentation.state

import com.jayjohn.app.feature.domain.model.WeatherData

data class ForecastEventState(
    val isLoading: Boolean = false,
    val data: WeatherData? = null,
    val error: String = ""
)
