package com.example.weatherapp.data.network.dto

import com.google.gson.annotations.SerializedName

data class DatWeatherDto(
    @SerializedName("avgtemp_c") val tepC: Float,
    @SerializedName("condition") val condition: ConditionDto
)