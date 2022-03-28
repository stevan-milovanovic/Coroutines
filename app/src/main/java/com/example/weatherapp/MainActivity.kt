package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.api.ApiManagerImpl
import com.example.weatherapp.data.api.RetrofitBuilder
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.utils.Status
import com.example.weatherapp.utils.ViewModelFactory
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<out String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissionRequest = registerForLocationPermissionRequest(
            ::fetchForecastForClosestCity,
            ::handleDeniedLocationPermission
        )

        setupViewModel()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.isVisible = true
        if (isLocationPermissionGranted()) {
            fetchForecastForClosestCity()
        } else {
            requestLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchForecastForClosestCity() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { lastKnownLocation ->
                lastKnownLocation?.let {
                    viewModel.findClosestCity(resources, it)
                } ?: run {
                    handleDisabledLocationServices()
                }
            }
            .addOnCanceledListener {
                showToast(R.string.last_location_request_has_been_canceled)
                handleDisabledLocationServices()
            }
            .addOnFailureListener {
                showToast(getString(R.string.last_location_request_failed, it.localizedMessage))
                handleDisabledLocationServices()
            }
    }

    private fun handleDisabledLocationServices() {
        binding.loadingOverlayView.isVisible = true
        binding.progressBar.isVisible = false
        binding.locationPermissionIssueTextView.apply {
            text = getString(R.string.enable_location_to_see_weather_forecast)
            isVisible = true
        }
    }

    private fun handleDeniedLocationPermission() {
        binding.loadingOverlayView.isVisible = true
        binding.progressBar.isVisible = false
        binding.locationPermissionIssueTextView.apply {
            text = getString(R.string.location_permissions_rationale)
            isVisible = true
        }
        showToast(R.string.location_permission_has_been_denied)
    }

    private fun showToast(@StringRes messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setupObserver() {
        viewModel.forecast.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.locationPermissionIssueTextView.isVisible = false
                    binding.loadingOverlayView.isVisible = false
                    binding.progressBar.isVisible = false
                    it.data?.let { forecast ->
                        binding.currentCityTextView.text = forecast.name
                        binding.weatherDescriptionValueTextView.text =
                            forecast.weather.first().description

                        binding.minTempValueTextView.text =
                            forecast.mainParameters?.minTemperature.toString()
                        binding.maxTempValueTextView.text =
                            forecast.mainParameters?.maxTemperature.toString()
                        binding.pressureValueTextView.text =
                            forecast.mainParameters?.pressure.toString()
                        binding.humidityValueTextView.text =
                            forecast.mainParameters?.humidity.toString()
                    }
                }
                Status.LOADING -> {
                    binding.loadingOverlayView.isVisible = true
                    binding.progressBar.isVisible = true
                }
                Status.ERROR -> {
                    binding.loadingOverlayView.isVisible = true
                    binding.progressBar.isVisible = false
                    it.message?.let { message ->
                        showToast(message)
                    }
                }
            }
        }

        viewModel.closestCity.observe(this) { closestCity ->
            closestCity?.let { viewModel.fetchForecast(it.provider.toInt()) }
        }
    }

    private fun registerForLocationPermissionRequest(
        permissionGrantedCallback: () -> Unit = {},
        permissionDeniedCallback: () -> Unit = {}
    ) = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                permissionGrantedCallback.invoke()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                permissionGrantedCallback.invoke()
            }
            else -> {
                // No location access granted.
                permissionDeniedCallback.invoke()
            }
        }
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun isLocationPermissionGranted(): Boolean {
        val fineLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        return fineLocation == PackageManager.PERMISSION_GRANTED || coarseLocation == PackageManager.PERMISSION_GRANTED
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiManagerImpl(RetrofitBuilder.apiService)
            )
        )[MainViewModel::class.java]
    }

}