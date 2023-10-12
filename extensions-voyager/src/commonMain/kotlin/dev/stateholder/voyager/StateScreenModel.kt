package dev.stateholder.voyager

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import dev.stateholder.StateHolder
import dev.stateholder.StateOwner
import dev.stateholder.StateProvider
import dev.stateholder.stateContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Suppress("MemberVisibilityCanBePrivate")
public abstract class StateScreenModel<State>(
    protected val stateHolder: StateHolder<State>,
) : ScreenModel, StateOwner<State> {

    public constructor(stateProvider: StateProvider<State>) : this(stateContainer(stateProvider))

    public constructor(initialState: State) : this(stateContainer(initialState))

    override val state: StateFlow<State> = stateHolder.state

    public fun <T> Flow<T>.collectToState(
        scope: CoroutineScope = coroutineScope,
        block: suspend (state: State, value: T) -> State,
    ): Job {
        return stateHolder.addSource(this, scope, block)
    }
}