package dev.stateholder.internal

import dev.stateholder.StateHolder
import dev.stateholder.StateOwner
import dev.stateholder.StateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Default implementation of [StateHolder].
 *
 * @param[State] The type of the state.
 * @param[initialStateProvider] The provider of the initial state.
 */
internal class DefaultStateHolder<State>(
    initialStateProvider: StateProvider<State>,
) : StateHolder<State> {

    private val _state = MutableStateFlow(initialStateProvider.provide())

    /**
     * @see StateHolder.state
     */
    override val state = _state.asStateFlow()

    /**
     * @see StateHolder.addSource
     */
    override fun <T> addSource(
        flow: Flow<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job = scope.launch {
        flow.collect { value ->
            _state.update { state -> block(state, value) }
        }
    }

    /**
     * @see StateHolder.addSource
     */
    override fun <T> addSource(
        holder: StateHolder<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job = addSource(holder.state, scope, block)

    /**
     * @see StateHolder.addSource
     */
    override fun <T> addSource(
        owner: StateOwner<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job = addSource(owner.state, scope, block)

    /**
     * Update the state in the container.
     *
     * This call must be wrapped in [MutableStateFlow.update] to preserve thread safety.
     * Without it [block] could be called with stale state.
     *
     * @see StateHolder.update
     */
    override fun update(block: (State) -> State) {
        _state.update { currentState ->
            val newState = block(_state.value)
            if (newState == _state.value) {
                currentState
            } else {
                newState
            }
        }
    }
}
