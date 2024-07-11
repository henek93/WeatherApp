package com.example.weatherapp.presentation.details.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.entity.Forecast
import com.example.weatherapp.domain.usecase.ChangeFavouriteStateUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import com.example.weatherapp.domain.usecase.ObserveFavouriteStateUseCase
import com.example.weatherapp.presentation.details.store.DetailStore.Intent
import com.example.weatherapp.presentation.details.store.DetailStore.Label
import com.example.weatherapp.presentation.details.store.DetailStore.State
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

interface DetailStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickChangeFavouriteStatus : Intent

        data object ClickBack : Intent
    }

    data class State(
        val city: City,
        val isFavourite: Boolean,
        val forecastState: ForecastState
    ) {

        sealed interface ForecastState {

            object Initial : ForecastState

            object Loading : ForecastState

            object Error : ForecastState

            data class Loaded(val forecast: Forecast) : ForecastState
        }
    }

    sealed interface Label {

        data object ClickBack : Label
    }
}

class DetailStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getForecastUseCase: GetForecastUseCase,
    private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
    private val observeFavouriteStateUseCase: ObserveFavouriteStateUseCase
) {

    fun create(city: City): DetailStore =
        object : DetailStore, Store<Intent, State, Label> by storeFactory.create(
            name = "DetailStore",
            initialState = State(
                city = city,
                isFavourite = false,
                forecastState = State.ForecastState.Initial
            ),
            bootstrapper = BootstrapperImpl(city),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class ForecastLoaded(val forecast: Forecast) : Action

        data object StartLoading : Action

        data object ForecastLoadingError : Action

        data class FavouriteStatusChanged(val isFavourite: Boolean) : Action
    }

    private sealed interface Msg {

        data class ForecastLoaded(val forecast: Forecast) : Msg

        data object StartLoading : Msg

        data object ForecastLoadingError : Msg

        data class FavouriteStatusChanged(val isFavourite: Boolean) : Msg

    }

    private inner class BootstrapperImpl(
        private val city: City
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                observeFavouriteStateUseCase(cityId = city.id).collect {
                    dispatch(Action.FavouriteStatusChanged(isFavourite = it))
                }
            }
            scope.launch {
                dispatch(Action.StartLoading)
                try {
                    val forecast = getForecastUseCase(cityId = city.id)
                    dispatch(Action.ForecastLoaded(forecast))
                } catch (e: Exception) {
                    dispatch(Action.ForecastLoadingError)
                }

            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }

                Intent.ClickChangeFavouriteStatus -> {
                    val state = getState()
                    if (state.isFavourite) {
                        scope.launch {
                            changeFavouriteStateUseCase.removeFromFavourite(state.city.id)
                        }

                    } else {
                        scope.launch {
                            changeFavouriteStateUseCase.addToFavourite(state.city)
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                Action.StartLoading -> {
                    dispatch(Msg.StartLoading)
                }

                Action.ForecastLoadingError -> {
                    dispatch(Msg.ForecastLoadingError)
                }

                is Action.ForecastLoaded -> {
                    val forecast = action.forecast
                    dispatch(Msg.ForecastLoaded(forecast = forecast))
                }

                is Action.FavouriteStatusChanged -> {
                    val isFavourite = action.isFavourite
                    dispatch(Msg.FavouriteStatusChanged(isFavourite))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg) = when (msg) {
            is Msg.FavouriteStatusChanged -> {
                copy(
                    isFavourite = msg.isFavourite,
                )
            }

            is Msg.ForecastLoaded -> {
                copy(
                    forecastState = State.ForecastState.Loaded(forecast = msg.forecast)
                )
            }

            Msg.ForecastLoadingError -> {
                copy(
                    forecastState = State.ForecastState.Error
                )
            }

            Msg.StartLoading -> {
                copy(
                    forecastState = State.ForecastState.Loading
                )
            }
        }
    }
}
