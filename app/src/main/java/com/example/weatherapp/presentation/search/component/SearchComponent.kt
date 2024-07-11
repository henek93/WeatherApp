package com.example.weatherapp.presentation.search.component

import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.presentation.search.store.SearchStore
import kotlinx.coroutines.flow.StateFlow

interface SearchComponent {

    val model: StateFlow<SearchStore.State>

    fun changeSearchQuery(searchQuery: String)

    fun clickBack()

    fun clickSearch()

    fun clickCity(city: City)

}