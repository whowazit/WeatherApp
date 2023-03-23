package com.jayjohn.app.di

import com.jayjohn.app.feature.data.datasource.remote.WeatherApi
import com.jayjohn.app.feature.data.repository.WeatherImpl
import com.jayjohn.app.feature.domain.repository.WeatherRepository
import com.jayjohn.app.feature.domain.usecase.GetForecastDaysUseCase
import com.jayjohn.app.feature.domain.usecase.GetLocationUseCase
import com.jayjohn.app.feature.domain.usecase.WeatherUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(weatherApi: WeatherApi): WeatherRepository =
        WeatherImpl(weatherApi)

    @Provides
    @Singleton
    fun provideWeatherUseCases(weatherRepository: WeatherRepository): WeatherUseCases {
        return WeatherUseCases(
            getForecastDaysUseCase = GetForecastDaysUseCase(weatherRepository = weatherRepository),
            getLocationUseCase = GetLocationUseCase(weatherRepository = weatherRepository),
        )
    }
}