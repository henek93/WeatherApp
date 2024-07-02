package com.example.weatherapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherapp.data.network.api.ApiFactory
import com.example.weatherapp.presentation.ui.theme.WeatherAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = ApiFactory.apiService

        CoroutineScope(Dispatchers.Main).launch {
            val currentWeatehr = apiService.loadCurrentWeather("London")
            val forecast = apiService.loadForecast("London")
            val cities = apiService.searchCity("London")
            Log.d(
                "MainActivity",
                "$currentWeatehr\n forecast: $forecast citirs: $cities"
            )
        }
        setContent {
            WeatherAppTheme {

            }
        }
    }
}
