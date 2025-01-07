package dev.stateholder.extensions.voyager

import dev.stateholder.EventHolder
import dev.stateholder.StateHolder
import dev.stateholder.StateProvider
import dev.stateholder.stateContainer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow

public abstract class UiStateScreenModel<State, Event>(
    stateHolder: StateHolder<State>,
) : StateScreenModel<State>(stateHolder), EventHolder<Event> {
    public constructor(stateProvider: StateProvider<State>) : this(stateContainer(stateProvider))

    public constructor(initialState: State) : this(stateContainer(initialState))

    protected val eventContainer: StateHolder<PersistentList<Event>> =
        stateContainer(persistentListOf())

    public override val events: StateFlow<PersistentList<Event>> = eventContainer.state

    override fun handle(event: Event) {
        eventContainer.update { it.remove(event) }
    }

    protected fun emit(event: Event) {
        eventContainer.update { it.add(event) }
    }
}