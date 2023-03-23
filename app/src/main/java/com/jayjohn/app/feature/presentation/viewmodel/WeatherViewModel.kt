package com.jayjohn.app.feature.presentation.viewmodel

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jayjohn.app.feature.domain.usecase.WeatherUseCases
import com.jayjohn.app.feature.presentation.state.ForecastEventState
import com.jayjohn.app.feature.presentation.state.SearchEventState
import com.jayjohn.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherUseCases: WeatherUseCases
): ViewModel() {

    private var forecastJob: Job? = null
    private var searchJob: Job? = null

    private val _forecast = MutableStateFlow(ForecastEventState())
    val forecast: StateFlow<ForecastEventState> = _forecast

    private val _search = MutableStateFlow(SearchEventState())
    val search: StateFlow<SearchEventState> = _search

    fun getForecastData(keyword: String) {
        forecastJob?.cancel()
        forecastJob = weatherUseCases.getForecastDaysUseCase.invoke(
            keyword = keyword
        )
            .onEach {
                when(it) {
                    is Resource.Loading -> {
                        _forecast.value = ForecastEventState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _forecast.value = ForecastEventState(data = it.data)
                    }
                    is Resource.Error -> {
                        _forecast.value = ForecastEventState(error = it.message ?: "")
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun searchLocation(keyword: String) {
        searchJob?.cancel()
        searchJob = weatherUseCases.getLocationUseCase.invoke(
            keyword = keyword
        )
            .onEach {
                when(it) {
                    is Resource.Loading -> {
                        _search.value = SearchEventState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _search.value = SearchEventState(data = it.data)
                    }
                    is Resource.Error -> {
                        _search.value = SearchEventState(error = it.message ?: "")
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadImage(context: Context, url: String, view: ImageView) {
        Glide.with(context)
            .load("https:${url}")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }
}