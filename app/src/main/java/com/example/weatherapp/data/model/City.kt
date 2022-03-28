package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("_id")
    val id: Int = 0,
    @SerializedName("main")
    val name: String = "",
    @SerializedName("coord")
    val coordinate: Coordinate = Coordinate()
)