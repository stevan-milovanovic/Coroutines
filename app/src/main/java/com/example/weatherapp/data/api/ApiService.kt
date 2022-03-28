package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.Forecast
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("id") cityId: Int,
        @Query("appid") appId: String
    ): Forecast

}