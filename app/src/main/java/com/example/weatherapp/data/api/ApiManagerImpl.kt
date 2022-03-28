package com.example.weatherapp.data.api

class ApiManagerImpl(private val apiService: ApiService) : ApiManager {

    override suspend fun getWeather(cityId: Int, appId: String) = apiService.getWeather(cityId, appId)

}