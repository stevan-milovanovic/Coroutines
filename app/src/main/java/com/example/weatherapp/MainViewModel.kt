package com.example.weatherapp

import android.content.res.Resources
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig.APP_ID
import com.example.weatherapp.data.api.ApiManager
import com.example.weatherapp.data.model.Forecast
import com.example.weatherapp.utils.CitiesLoader
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class MainViewModel(
    private val apiManager: ApiManager
) : ViewModel() {

    val closestCity = MutableLiveData<Location?>()
    val forecast = MutableLiveData<Resource<Forecast>>()

    fun fetchForecast(cityId: Int) {
        viewModelScope.launch {
            forecast.value = Resource.loading(null)
            try {
                val forecastFromApi = withContext(Dispatchers.IO) {
                    apiManager.getWeather(cityId, APP_ID)
                }
                forecast.value = Resource.success(forecastFromApi)
            } catch (e: UnknownHostException) {
                forecast.value = Resource.error(e.message ?: "Unknown host exception occurred", null)
            }
        }
    }

    fun findClosestCity(resources: Resources, lastKnownLocation: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            closestCity.postValue(CitiesLoader.findClosestCity(resources, lastKnownLocation))
        }
    }

}