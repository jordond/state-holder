package dev.stateholder.extensions.voyager

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.stateholder.StateContainer
import dev.stateholder.StateHolder
import dev.stateholder.StateProvider
import dev.stateholder.stateContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Suppress("MemberVisibilityCanBePrivate")
public abstract class StateScreenModel<State>(
    protected val stateContainer: StateContainer<State>,
) : ScreenModel, StateHolder<State> {

    public constructor(stateProvider: StateProvider<State>) : this(stateContainer(stateProvider))

    public constructor(initialState: State) : this(stateContainer(initialState))

    override val state: StateFlow<State> = stateContainer.state

    protected fun <T> Flow<T>.mergeState(
        scope: CoroutineScope = screenModelScope,
        block: suspend (state: State, value: T) -> State,
    ): Job {
        return stateContainer.merge(this, scope, block)
    }

    protected fun <T> StateHolder<T>.mergeState(
        scope: CoroutineScope = screenModelScope,
        block: suspend (state: State, value: T) -> State,
    ): Job = state.mergeState(scope, block)

    protected fun updateState(block: (State) -> State) {
        stateContainer.update(block)
    }
}