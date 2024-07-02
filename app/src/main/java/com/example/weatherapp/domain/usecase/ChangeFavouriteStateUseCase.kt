package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class ChangeFavouriteStateUseCase @Inject constructor(
    private val repository: FavouriteRepository
) {

    suspend fun removeFromFavourite(cityId: Int) = repository.removeFromFavourite(cityId)

    suspend fun addToFavourite(city: City) = repository.addToFavourite(city)
}