package com.example.weatherapp.presentation.details.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.weatherapp.presentation.details.store.DetailStore.Intent
import com.example.weatherapp.presentation.details.store.DetailStore.Label
import com.example.weatherapp.presentation.details.store.DetailStore.State

internal interface DetailStore : Store<Intent, State, Label> {

    sealed interface Intent {
    }

    data class State(val todo: Unit)

    sealed interface Label {
    }
}

internal class DetailStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): DetailStore =
        object : DetailStore, Store<Intent, State, Label> by storeFactory.create(
            name = "DetailStore",
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
        override fun executeIntent(intent: Intent) {
            super.executeIntent(intent)
        }

        override fun executeAction(action: Action) {
            super.executeAction(action)
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg) = State(Unit)
    }
}
