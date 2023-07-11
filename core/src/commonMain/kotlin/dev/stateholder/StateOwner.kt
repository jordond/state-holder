package dev.stateholder

import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes a [state] property that can be used to access the state of a [StateHolder].
 */
public interface StateOwner<State> {

    public val state: StateFlow<State>

    public companion object {

        /**
         * Create a state owner from the given [stateHolder].
         */
        internal fun <State> from(
            stateHolder: StateHolder<State>,
        ): StateOwner<State> = object : StateOwner<State> {
            override val state: StateFlow<State> get() = stateHolder.state
        }
    }
}

/**
 * Create a [StateOwner] from the given [StateHolder].
 *
 * This is primarily used when delegating to the [StateHolder.state] property.
 *
 * Example:
 *
 * ```
 * class MyModel(
 *    private val container: StateContainer<Boolean>
 * ) : ViewModel(), StateOwner<Boolean> by container.asStateOwner() {
 *  // ...
 * }
 * ```
 */
public fun <State> StateHolder<State>.asStateOwner(): StateOwner<State> {
    return StateOwner.from(this)
}
