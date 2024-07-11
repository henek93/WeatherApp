package com.example.weatherapp.presentation.root.component

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.weatherapp.presentation.details.component.DetailsComponent
import com.example.weatherapp.presentation.favourite.component.FavouriteComponent
import com.example.weatherapp.presentation.search.component.SearchComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child{

        data class Favourite(val component: FavouriteComponent): Child

        data class Details(val component: DetailsComponent): Child

        data class Search(val component: SearchComponent): Child
    }
}