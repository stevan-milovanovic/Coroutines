package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("weather")
    val weather: List<Weather> = emptyList(),
    @SerializedName("main")
    val mainParameters: MainParameters? = null,
    @SerializedName("name")
    val name: String = ""
)