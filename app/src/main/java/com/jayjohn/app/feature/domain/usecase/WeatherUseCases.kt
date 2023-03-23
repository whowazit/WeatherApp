package com.jayjohn.app.feature.domain.usecase

data class WeatherUseCases(
    val getForecastDaysUseCase: GetForecastDaysUseCase,
    val getLocationUseCase: GetLocationUseCase,
)
