package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class MainParameters(
    @SerializedName("temp_min")
    val minTemperature: Double = 0.0,
    @SerializedName("temp_max")
    val maxTemperature: Double = 0.0,
    @SerializedName("pressure")
    val pressure: Double = 0.0,
    @SerializedName("humidity")
    val humidity: Double = 0.0,
)