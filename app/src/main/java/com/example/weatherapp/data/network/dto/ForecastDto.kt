package com.example.weatherapp.data.network.dto

import com.example.weatherapp.domain.entity.Forecast
import com.google.gson.annotations.SerializedName

data class ForecastDto(
    @SerializedName("forecastday") val forecastDay: List<DayDto>
)