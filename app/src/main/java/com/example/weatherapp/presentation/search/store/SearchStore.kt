package com.example.weatherapp.presentation.search.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.usecase.ChangeFavouriteStateUseCase
import com.example.weatherapp.domain.usecase.SearchCityUseCase
import com.example.weatherapp.presentation.search.OpenReason
import com.example.weatherapp.presentation.search.store.SearchStore.Intent
import com.example.weatherapp.presentation.search.store.SearchStore.Label
import com.example.weatherapp.presentation.search.store.SearchStore.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data class ChangeSearchQuery(val searchQuery: String) : Intent

        data object ClickBack : Intent

        data object ClickSearch : Intent

        data class ClickCity(val city: City) : Intent
    }

    data class State(
        val searchQuery: String,
        val searchState: SearchState
    ) {

        sealed interface SearchState {

            data object Initial : SearchState

            data object Searching : SearchState

            data object Error : SearchState

            data object EmptyResult : SearchState

            data class Searched(
                val cities: List<City>
            ) : SearchState
        }
    }

    sealed interface Label {

        data object ClickBack : Label

        data object SavedToFavourite : Label

        data class OpenForecast(val city: City) : Label
    }
}

class SearchStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val searchCityUseCase: SearchCityUseCase,
    private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase
) {

    fun create(openReason: OpenReason): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(
                searchQuery = "",
                searchState = State.SearchState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl(openReason) },
            reducer = ReducerImpl
        ) {}

    private sealed interface Action


    private sealed interface Msg {

        data class ChangeQuery(val query: String) : Msg

        data object LoadingSearchResult : Msg

        data class SuccessLoading(val cities: List<City>) : Msg

        data object ErrorLoading : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private inner class ExecutorImpl(private val openReason: OpenReason) :
        CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        private var searchJob: Job? = null

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.ChangeSearchQuery -> {
                    dispatch(Msg.ChangeQuery(query = intent.searchQuery))
                }

                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }

                is Intent.ClickCity -> {
                    when (openReason) {
                        OpenReason.RegularSearch -> {
                            publish(Label.OpenForecast(city = intent.city))
                        }

                        OpenReason.AddToFavourite -> {
                            scope.launch {
                                changeFavouriteStateUseCase.addToFavourite(city = intent.city)
                                publish(Label.SavedToFavourite)
                            }
                        }
                    }
                    publish(Label.OpenForecast(city = intent.city))
                }

                Intent.ClickSearch -> {
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        dispatch(Msg.LoadingSearchResult)
                        try {
                            val cities = searchCityUseCase(getState().searchQuery)
                            dispatch(Msg.SuccessLoading(cities = cities))
                        } catch (e: Exception) {
                            dispatch(Msg.ErrorLoading)
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ChangeQuery -> {
                    copy(
                        searchQuery = msg.query
                    )
                }

                Msg.ErrorLoading -> {
                    copy(
                        searchState = State.SearchState.Error
                    )
                }

                Msg.LoadingSearchResult -> {
                    copy(
                        searchState = State.SearchState.Searching
                    )
                }

                is Msg.SuccessLoading -> {
                    val searchState = if (msg.cities.isEmpty()) {
                        State.SearchState.EmptyResult
                    } else {
                        State.SearchState.Searched(cities = msg.cities)
                    }
                    copy(
                        searchState = searchState
                    )
                }
            }
    }
}
