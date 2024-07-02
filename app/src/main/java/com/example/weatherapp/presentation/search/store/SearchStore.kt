package com.example.weatherapp.presentation.search.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.weatherapp.presentation.search.store.SearchStore.Intent
import com.example.weatherapp.presentation.search.store.SearchStore.Label
import com.example.weatherapp.presentation.search.store.SearchStore.State

internal interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {
    }

    data class State(val todo: Unit)

    sealed interface Label {
    }
}

internal class SearchStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(Unit),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
    }

    private sealed interface Msg {
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State = State(Unit)
    }
}
