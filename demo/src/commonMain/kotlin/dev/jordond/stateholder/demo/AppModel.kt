package dev.jordond.stateholder.demo

import dev.stateholder.extensions.voyager.UiStateScreenModel

class AppModel : UiStateScreenModel<AppModel.State, AppModel.Event>(State()) {
    fun increment() {
        updateState { it.copy(count = it.count + 1) }
    }

    fun decrement() {
        updateState { it.copy(count = it.count - 1) }
    }

    fun setCount(count: Int) {
        updateState { it.copy(count = count) }
    }

    fun triggerEvent() {
        emit(Event.Toast)
    }

    data class State(
        val count: Int = 0,
    )

    sealed interface Event {
        data object Toast : Event
    }
}