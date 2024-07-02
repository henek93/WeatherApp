package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.network.dto.WeatherCurrentDto
import com.example.weatherapp.data.network.dto.WeatherDto
import com.example.weatherapp.data.network.dto.WeatherForecastDto
import com.example.weatherapp.domain.entity.Forecast
import com.example.weatherapp.domain.entity.Weather
import java.util.Calendar
import java.util.Date

fun WeatherCurrentDto.toEntity() = current.toEntity()

fun WeatherDto.toEntity() = Weather(
    tempC = tempC,
    conditionText = conditionDto.text,
    conditionUrl = conditionDto.iconUrl.correctImageUrl(),
    date = date.toCalendar()
)

fun WeatherForecastDto.toEntity() = Forecast(
    currentWeather = current.toEntity(),
    upcoming = forecast.forecastDay.drop(1).map { dayDto ->
        val dayWeatherDto = dayDto.dayWeatherDto
        Weather(
            tempC = dayWeatherDto.tepC,
            conditionText = dayWeatherDto.condition.text,
            conditionUrl = dayWeatherDto.condition.iconUrl.correctImageUrl(),
            date = dayDto.date.toCalendar()
        )
    }
)

private fun Long.toCalendar() = Calendar.getInstance().apply {
    time = Date(this@toCalendar * 1000)
}

private fun String.correctImageUrl() =
    "https:$this".replace(oldValue = "64x64", newValue = "128x128")