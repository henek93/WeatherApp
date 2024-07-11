package com.example.weatherapp.presentation.details.component

import com.example.weatherapp.presentation.details.store.DetailStore
import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {

    val model: StateFlow<DetailStore.State>

    fun onClickChangeFavouriteStatus()

    fun clickBack()
}