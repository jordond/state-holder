package dev.jordond.stateholder.demo

import dev.stateholder.extensions.voyager.StateScreenModel


class AppModel : StateScreenModel<AppModel.State>(State()) {
    fun increment() {
        updateState { it.copy(count = it.count + 1) }
    }

    fun decrement() {
        updateState { it.copy(count = it.count - 1) }
    }

    fun triggerEvent() {

    }

    data class State(
        val count: Int = 0,
    )

    sealed interface Event {
        data object Toast : Event
    }
}