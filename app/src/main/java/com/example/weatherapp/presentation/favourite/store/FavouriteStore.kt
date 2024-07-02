package com.example.weatherapp.presentation.favourite.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.usecase.GetFavouriteCitiesUseCase
import com.example.weatherapp.domain.usecase.GetWeatherUseCase
import com.example.weatherapp.presentation.favourite.store.FavouriteStore.Intent
import com.example.weatherapp.presentation.favourite.store.FavouriteStore.Label
import com.example.weatherapp.presentation.favourite.store.FavouriteStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface FavouriteStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickSearch : Intent

        data object ClickToFavourite : Intent

        data class CityItemClick(val city: City) : Intent
    }

    data class State(
        val cityItems: List<CityItem>
    ) {

        data class CityItem(
            val city: City,
            val weatherState: WeatherState
        )

        sealed interface WeatherState {

            data object Initial : WeatherState

            data object Loading : WeatherState

            data object Error : WeatherState

            data class Loaded(
                val tempC: Float,
                val iconUrl: String
            ) : WeatherState
        }
    }

    sealed interface Label {

        data object ClickSearch : Label

        data object ClickToFavourite : Label

        data class CityItemClick(val city: City) : Label
    }
}

class FavouriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getFavouriteCitiesUseCase: GetFavouriteCitiesUseCase,
    private val getWeatherUseCase: GetWeatherUseCase
) {

    fun create(): FavouriteStore =
        object : FavouriteStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FavouriteStore",
            initialState = State(listOf()),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class FavouriteCitiesLoaded(val cities: List<City>) : Action
    }

    private sealed interface Msg {

        data class FavouriteCitiesLoaded(val cities: List<City>) : Msg

        data class WeatherLoaded(
            val cityId: Int,
            val tempC: Float,
            val conditionUrl: String
        ) : Msg

        data class WeatherloadingError(
            val cityId: Int
        ) : Msg

        data class WeatherIsLoading(
            val cityId: Int
        ) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getFavouriteCitiesUseCase().collect {
                    dispatch(Action.FavouriteCitiesLoaded(it))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.CityItemClick -> {
                    publish(Label.CityItemClick(intent.city))
                }

                is Intent.ClickToFavourite -> {
                    publish(Label.ClickToFavourite)
                }

                is Intent.ClickSearch -> {
                    publish(Label.ClickSearch)
                }
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.FavouriteCitiesLoaded -> {
                    val cities = action.cities
                    dispatch(Msg.FavouriteCitiesLoaded(cities))
                    cities.forEach {
                        scope.launch {
                            loadWeatherForCity(city = it)
                        }
                    }
                }
            }
        }

        private suspend fun loadWeatherForCity(city: City) {
            dispatch(Msg.WeatherIsLoading(city.id))

            try {
                val weather = getWeatherUseCase(cityId = city.id)
                dispatch(
                    Msg.WeatherLoaded(
                        cityId = city.id,
                        tempC = weather.tempC,
                        conditionUrl = weather.conditionUrl
                    )
                )
            } catch (e: Exception) {
                dispatch(
                    Msg.WeatherloadingError(cityId = city.id)
                )
            }

        }
    }

    private object ReducerImpl : Reducer<State, Msg> {

        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.FavouriteCitiesLoaded -> {
                copy(
                    cityItems = msg.cities.map {
                        State.CityItem(
                            city = it,
                            weatherState = State.WeatherState.Initial
                        )
                    }
                )
            }

            is Msg.WeatherIsLoading -> {
                copy(
                    cityItems = cityItems.map {
                        if (it.city.id == msg.cityId) {
                            it.copy(weatherState = State.WeatherState.Loading)
                        } else {
                            it
                        }
                    }
                )
            }

            is Msg.WeatherLoaded -> {
                copy(
                    cityItems = cityItems.map {
                        if (it.city.id == msg.cityId) {
                            it.copy(
                                weatherState = State.WeatherState.Loaded(
                                    msg.tempC,
                                    msg.conditionUrl
                                )
                            )
                        } else {
                            it
                        }
                    }
                )
            }

            is Msg.WeatherloadingError -> {
                copy(
                    cityItems = cityItems.map {
                        if (it.city.id == msg.cityId){
                            it.copy(weatherState = State.WeatherState.Error)
                        } else{
                            it
                        }
                    }
                )
            }
        }
    }
}
