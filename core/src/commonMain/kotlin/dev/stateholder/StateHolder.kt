package dev.stateholder

import dev.stateholder.internal.DefaultStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty

/**
 * A container for managing state.
 *
 * The [state] property is the only property that should be exposed to the UI level. All updating
 * should be handled separately.
 *
 * Example usage:
 *
 *
 *
 * @param[State] The type of the state.
 */
public interface StateHolder<State> {

    /**
     * The current state of type [State] exposed as a [StateFlow].
     */
    public val state: StateFlow<State>

    /**
     * Add a source of state from another [Flow].
     *
     * This is useful when you need to update the [state] based off of another [Flow]. The [flow]
     * will be collected and [block] will be invoked in order to map the [T] value from [flow] to
     * the [State] value.
     *
     * The collecting can be stopped by cancelling the returned [Job].
     *
     * @param[T] The type of the value from flow.
     * @param[flow] The flow to collect from and update state with.
     * @param[scope] The scope to use for collecting the flow.
     * @param[block] The function to map the [T] value from [flow] to the [State] value.
     * @return The [Job] of the collection.
     */
    public fun <T> addSource(
        flow: Flow<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job

    /**
     * Combine the state with another [StateHolder].
     *
     * This is useful when you need to update the [state] based off of another [StateHolder]. The
     * [holder] will be observed and [block] will be invoked in order to map the [T] value from
     * [holder] to the [State] value.
     *
     * The observing can be stopped by cancelling the returned [Job].
     *
     * @param[T] The type of the value from the [holder].
     * @param[holder] The [StateHolder] to observe and update state with.
     * @param[scope] The scope to use for observing the [holder].
     * @return The [Job] of the observation.
     */
    public fun <T> combine(
        holder: StateHolder<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job

    /**
     * Combine the state with another [StateOwner].
     *
     * This is useful when you need to update the [state] based off of another [StateOwner]. The
     * [owner] will be observed and [block] will be invoked in order to map the [T] value from
     * [owner] to the [State] value.
     *
     * The observing can be stopped by cancelling the returned [Job].
     *
     * @param[T] The type of the value from the [owner].
     * @param[owner] The [StateOwner] to observe and update state with.
     * @param[scope] The scope to use for observing the [owner].
     * @return The [Job] of the observation.
     */
    public fun <T> combine(
        owner: StateOwner<T>,
        scope: CoroutineScope,
        block: suspend (State, T) -> State,
    ): Job

    /**
     * Updates the MutableStateFlow.value atomically using the specified function of its value.
     * function may be evaluated multiple times, if value is being concurrently updated.
     */
    public fun update(block: (State) -> State)

    /**
     * Allows using delegation to access the [StateHolder.state] property.
     *
     * Example:
     *
     * ```
     * val state: StateFlow<State> by stateContainer
     * ```
     */
    public operator fun getValue(
        stateOwner: StateOwner<State>,
        property: KProperty<*>,
    ): StateFlow<State> = state

    public companion object {

        /**
         * Create a [StateHolder] with the given [initialStateProvider].
         */
        internal fun <State> create(
            initialStateProvider: StateProvider<State>,
        ): StateHolder<State> = DefaultStateHolder(initialStateProvider)
    }
}

/**
 * Create a [StateHolder] with the given [initialStateProvider].
 *
 * @see [StateHolder]
 */
public fun <State> stateContainer(
    initialStateProvider: StateProvider<State>,
): StateHolder<State> = StateHolder.create(initialStateProvider)

/**
 * Create a [StateHolder] with the given [initialState].
 *
 * @see [StateHolder]
 */
public fun <State> stateContainer(
    initialState: State,
): StateHolder<State> = StateHolder.create(provideState(initialState))

public fun <T, State> Flow<T>.collectToState(
    container: StateHolder<State>,
    scope: CoroutineScope,
    block: suspend (state: State, value: T) -> State,
): Job {
    return container.addSource(this, scope, block)
}
