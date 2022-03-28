package com.example.weatherapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.data.api.ApiManager

class ViewModelFactory(
    private val apiManager: ApiManager
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiManager) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}