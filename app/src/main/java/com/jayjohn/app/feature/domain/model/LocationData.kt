package com.jayjohn.app.feature.domain.model

import com.google.gson.annotations.SerializedName

data class LocationData(
    val name: String,
    val region: String,
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lon")
    val longitude: Double,
)
