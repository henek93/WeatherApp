package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.db.FavouriteCitiesDao
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.repository.FavouriteRepository
import com.example.weatherapp.data.mapper.toDbModel
import com.example.weatherapp.data.mapper.toEntities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val favouriteCitiesDao: FavouriteCitiesDao
) : FavouriteRepository {
    override val favouriteCities = favouriteCitiesDao.getFavouriteCities()
        .map { it.toEntities() }

    override fun observeIsFavourite(cityId: Int) = favouriteCitiesDao.observeIsFavourite(cityId)

    override suspend fun addToFavourite(city: City) {
        favouriteCitiesDao.addToFavourite(city.toDbModel())
    }

    override suspend fun removeFromFavourite(cityId: Int) {
        favouriteCitiesDao.removeFromFavourite(cityId)
    }
}