package com.jayjohn.app.feature.presentation.state

import com.jayjohn.app.feature.domain.model.LocationData

data class SearchEventState(
    val isLoading: Boolean = false,
    val data: List<LocationData>? = null,
    val error: String = ""
)
