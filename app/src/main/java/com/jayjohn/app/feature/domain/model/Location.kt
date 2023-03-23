package com.jayjohn.app.feature.domain.model

import com.google.gson.annotations.SerializedName

data class Location(
    val name: String,
    @SerializedName("localtime")
    val local_time: String,
)
