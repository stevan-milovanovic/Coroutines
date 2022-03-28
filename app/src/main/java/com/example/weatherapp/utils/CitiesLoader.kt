package com.example.weatherapp.utils

import android.content.res.Resources
import android.location.Location
import com.example.weatherapp.R
import com.example.weatherapp.data.model.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CitiesLoader {

    fun findClosestCity(resources: Resources, lastKnownLocation: Location): Location? {
        var currentClosestLocation: Location? = null
        var minDistance = Float.MAX_VALUE
        for (location in loadCities(resources)) {
            val currentDistance = lastKnownLocation.distanceTo(location)
            if (currentDistance < minDistance) {
                minDistance = currentDistance
                currentClosestLocation = location
            }
        }
        return currentClosestLocation
    }

    private fun loadCities(resources: Resources): List<Location> {
        val locations = mutableListOf<Location>()
        var inputStream: InputStream? = null
        var reader: InputStreamReader? = null
        try {
            inputStream = resources.openRawResource(R.raw.city_list)
            val citiesType = object : TypeToken<Array<City>>() {}.type
            reader = InputStreamReader(inputStream)
            val cities = Gson().fromJson<Array<City>>(reader, citiesType)
            for (city in cities) {
                locations.add(Location(city.id.toString()).apply {
                    longitude = city.coordinate.longitude
                    latitude = city.coordinate.latitude
                })
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return locations
        } finally {
            reader?.close()
            inputStream?.close()
        }
        return locations
    }

}