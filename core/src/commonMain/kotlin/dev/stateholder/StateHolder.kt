package dev.stateholder

import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes a [state] property that can be used to access the state of a [StateContainer].
 */
public interface StateHolder<State> {

    /**
     * Returns the current state of the [StateContainer].
     */
    public val state: StateFlow<State>

    public companion object {

        /**
         * Create a state owner from the given [container].
         */
        internal fun <State> from(
            container: StateContainer<State>,
        ): StateHolder<State> = object : StateHolder<State> {
            override val state: StateFlow<State> get() = container.state
        }
    }
}

/**
 * Create a [StateHolder] from the given [StateContainer].
 *
 * This is primarily used when delegating to the [StateContainer.state] property.
 *
 * Example:
 *
 * ```
 * class MyModel(
 *    private val container: StateContainer<Boolean>
 * ) : StateOwner<Boolean> by container.asStateOwner() {
 *  // ...
 * }
 * ```
 */
public fun <State> StateContainer<State>.asStateOwner(): StateHolder<State> {
    return StateHolder.from(this)
}
