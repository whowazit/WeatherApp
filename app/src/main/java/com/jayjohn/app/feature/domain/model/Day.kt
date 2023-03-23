package com.jayjohn.app.feature.domain.model

import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("maxtemp_f")
    val maxTempF: Float,
    @SerializedName("mintemp_f")
    val minTempF: Float,
    val condition: Condition,
)
