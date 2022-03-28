package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.Forecast

interface ApiManager {

    suspend fun getWeather(cityId: Int, appId: String): Forecast

}