package dev.stateholder

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Collect emissions from a [StateContainer] launched and scoped to the [lifecycleOwner].
 *
 * @param[State] Type of collected state.
 * @param[lifecycleOwner] [LifecycleOwner] to launch coroutine from.
 * @param[minActiveState] The [Lifecycle.State] to launch the coroutine.
 * @param[context] The [CoroutineContext] to launch the coroutine.
 * @param[block] Lambda to handle the state emissions.
 */
public fun <State> StateHolder<State>.collectState(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: (State) -> Unit,
): Job = state.lifecycleCollect(lifecycleOwner, minActiveState, context, block)

/**
 * Collect emissions of a selected property of [State].
 *
 * @param[State] Type of collected state.
 * @param[T] Type of the selected property.
 * @param[lifecycleOwner] [LifecycleOwner] to launch coroutine from.
 * @param[selector] Lambda to select a property of [State] to collect.
 * @param[minActiveState] The [Lifecycle.State] to launch the coroutine.
 * @param[context] The [CoroutineContext] to launch the coroutine.
 * @param[block] Lambda to handle the selected property emissions.
 */
public fun <State, T> StateHolder<State>.collectState(
    lifecycleOwner: LifecycleOwner,
    selector: (State) -> T,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit,
): Job = state.map(selector).lifecycleCollect(lifecycleOwner, minActiveState, context, block)

/**
 * Launch a coroutine and repeat the [Flow.collect] on the given [minActiveState].
 *
 * @see LifecycleOwner.repeatOnLifecycle
 */
private fun <T> Flow<T>.lifecycleCollect(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State,
    context: CoroutineContext,
    block: (T) -> Unit,
): Job = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(minActiveState) {
        if (context == EmptyCoroutineContext) {
            collect(block)
        } else {
            withContext(context) {
                collect(block)
            }
        }
    }
}