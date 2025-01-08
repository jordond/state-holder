package dev.stateholder.internal

import dev.stateholder.StateContainer
import dev.stateholder.StateHolder
import dev.stateholder.StateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Default implementation of [StateContainer].
 *
 * @param[State] The type of the state.
 * @param[initialStateProvider] The provider of the initial state.
 */
internal class DefaultStateContainer<State>(
    initialStateProvider: StateProvider<State>,
) : StateContainer<State> {

    private val _state = MutableStateFlow(initialStateProvider.provide())

    /**
     * @see StateContainer.state
     */
    override val state = _state.asStateFlow()

    /**
     * @see StateContainer.mergeState
     */
    override fun <T> mergeState(
        flow: Flow<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job = scope.launch {
        flow.collect { value ->
            _state.update { state -> block(state, value) }
        }
    }

    /**
     * @see StateContainer.mergeState
     */
    override fun <T> mergeState(
        container: StateContainer<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job = mergeState(container.state, scope, block)

    /**
     * @see StateContainer.mergeState
     */
    override fun <T> mergeState(
        owner: StateHolder<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job = mergeState(owner.state, scope, block)

    /**
     * Update the state in the container.
     *
     * This call must be wrapped in [MutableStateFlow.update] to preserve thread safety.
     * Without it [block] could be called with stale state.
     *
     * @see StateContainer.update
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
