package com.example.weatherapp.presentation.favourite.component

import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.presentation.favourite.store.FavouriteStore
import kotlinx.coroutines.flow.StateFlow

interface FavouriteComponent {

    val model: StateFlow<FavouriteStore.State>

    fun onClickSearch()

    fun onClickAddFavourite()

    fun onCityItemClick(city: City)
}