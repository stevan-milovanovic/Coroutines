package com.example.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherapp.data.api.ApiManager
import com.example.weatherapp.data.model.Forecast
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var apiManager: ApiManager

    @Mock
    private lateinit var forecastObserver: Observer<Resource<Forecast>>

    @Test
    fun `test fetch forecast returning success`() {
        testCoroutineRule.runBlockingTest {
            val testForecast = Forecast()
            doReturn(testForecast)
                .`when`(apiManager)
                .getWeather(anyInt(), anyString())

            val viewModel = MainViewModel(apiManager)
            viewModel.forecast.observeForever(forecastObserver)

            viewModel.fetchForecast(12345)

            verify(apiManager).getWeather(anyInt(), anyString())
            verify(forecastObserver).onChanged(Resource.success(testForecast))
            viewModel.forecast.removeObserver(forecastObserver)
        }
    }

}