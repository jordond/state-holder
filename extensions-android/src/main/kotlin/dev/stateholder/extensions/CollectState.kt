package dev.stateholder.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import dev.stateholder.StateHolder
import dev.stateholder.StateOwner
import dev.stateholder.collectState
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Collect emissions from a [StateHolder] launched and scoped to [Fragment.getViewLifecycleOwner].
 *
 * Example:
 *
 * ```
 * class MyFragment : Fragment() {
 *     private val viewModel by viewModels<MyViewModel>()
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *
 *         collectState(viewModel) { state ->
 *             // Handle state
 *         }
 *     }
 * }
 * ```
 *
 * @param[State] Type of collected state.
 * @param[stateOwner] The [StateOwner] to collect state from.
 * @param[minActiveState] The [Lifecycle.State] to launch the coroutine.
 * @param[context] Coroutine context to launch the coroutine.
 * @param[block] Lambda to handle the state emissions.
 */
public fun <State> Fragment.collectState(
    stateOwner: StateOwner<State>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: (State) -> Unit,
): Job = stateOwner.collectState(viewLifecycleOwner, minActiveState, context, block)

/**
 * Collect emissions of a selected property of [State] scoped to [Fragment.getViewLifecycleOwner].
 *
 * Example:
 *
 * ```
 * class MyFragment : Fragment() {
 *     private val viewModel by viewModels<MyViewModel>()
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *
 *         collectState(viewModel, { it.myProperty }) { myProperty ->
 *             // Handle myProperty
 *         }
 *     }
 * }
 * ```
 *
 * @param[State] Type of collected state.
 * @param[T] Type of the selected property.
 * @param[stateOwner] The [StateOwner] to collect state from.
 * @param[selector] Lambda to select a property of [State] to collect.
 * @param[minActiveState] The [Lifecycle.State] to launch the coroutine.
 * @param[context] Coroutine context to launch the coroutine.
 * @param[block] Lambda to handle the selected property emissions.
 */
public fun <State, T> Fragment.collectState(
    stateOwner: StateOwner<State>,
    selector: (State) -> T,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit,
): Job = stateOwner.collectState(viewLifecycleOwner, selector, minActiveState, context, block)

/**
 * Collect emissions from a [StateHolder] launched and scoped to [FragmentActivity] lifecycle.
 *
 * Example:
 *
 * ```
 * class MyActivity : FragmentActivity() {
 *     private val viewModel by viewModels<MyViewModel>()
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         collectState(viewModel) { state ->
 *             // Handle state
 *         }
 *     }
 * }
 * ```
 *
 * @param[State] Type of collected state.
 * @param[stateOwner] The [StateOwner] to collect state from.
 * @param[minActiveState] The [Lifecycle.State] to launch the coroutine.
 * @param[context] Coroutine context to launch the coroutine.
 * @param[block] Lambda to handle the state emissions.
 */
public fun <State> FragmentActivity.collectState(
    stateOwner: StateOwner<State>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: (State) -> Unit,
): Job = stateOwner.collectState(lifecycleOwner = this, minActiveState, context, block)

/**
 * Collect emissions of a selected property of [State] scoped to [FragmentActivity] lifecycle.
 *
 * Example:
 *
 * ```
 * class MyActivity : FragmentActivity() {
 *     private val viewModel by viewModels<MyViewModel>()
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         collectState(viewModel, { it.myProperty }) { myProperty ->
 *             // Handle myProperty
 *         }
 *     }
 * }
 * ```
 *
 * @param[State] Type of collected state.
 * @param[T] Type of the selected property.
 * @param[selector] Lambda to select a property of [State] to collect.
 * @param[minActiveState] The [Lifecycle.State] to launch the coroutine.
 * @param[context] Coroutine context to launch the coroutine.
 * @param[block] Lambda to handle the selected property emissions.
 */
public fun <State, T> FragmentActivity.collectState(
    stateOwner: StateOwner<State>,
    selector: (State) -> T,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit,
): Job = stateOwner.collectState(this, selector, minActiveState, context, block)

