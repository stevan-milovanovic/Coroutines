package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class Coordinate(
    @SerializedName("lon")
    val longitude: Double = 0.0,
    @SerializedName("lat")
    val latitude: Double = 0.0,
)