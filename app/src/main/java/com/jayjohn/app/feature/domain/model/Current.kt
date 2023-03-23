package com.jayjohn.app.feature.domain.model

import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("temp_f")
    val currentTemp: Float,
    @SerializedName("wind_mph")
    val windMph: Double,
    val humidity: Int,
    @SerializedName("last_updated")
    val lastUpdated: String,
)
