package dev.stateholder.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.stateholder.StateHolder
import dev.stateholder.StateOwner
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
 * @param[lifecycleState] The [Lifecycle.State] to launch the coroutine.
 * @param[block] Lambda to handle the state emissions.
 */
public fun <State> Fragment.collectState(
    stateOwner: StateOwner<State>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (State) -> Unit,
): Job = stateOwner.collectState(viewLifecycleOwner, lifecycleState, block)

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
 * @param[selector] Lambda to select a property of [State] to collect.
 * @param[lifecycleState] The [Lifecycle.State] to launch the coroutine.
 * @param[block] Lambda to handle the selected property emissions.
 */
public fun <State, T> Fragment.collectState(
    stateOwner: StateOwner<State>,
    selector: (State) -> T,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (T) -> Unit,
): Job = stateOwner.collectState(viewLifecycleOwner, selector, lifecycleState, block)

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
 * @param[lifecycleState] The [Lifecycle.State] to launch the coroutine.
 * @param[block] Lambda to handle the state emissions.
 */
public fun <State> FragmentActivity.collectState(
    stateOwner: StateOwner<State>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (State) -> Unit,
): Job = stateOwner.collectState(lifecycleOwner = this, lifecycleState, block)

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
 * @param[lifecycleState] The [Lifecycle.State] to launch the coroutine.
 * @param[block] Lambda to handle the selected property emissions.
 */
public fun <State, T> FragmentActivity.collectState(
    stateOwner: StateOwner<State>,
    selector: (State) -> T,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (T) -> Unit,
): Job = stateOwner.collectState(this, selector, lifecycleState, block)

/**
 * Collect emissions from a [StateHolder] launched and scoped to the [lifecycleOwner].
 *
 * @param[State] Type of collected state.
 * @param[lifecycleOwner] [LifecycleOwner] to launch coroutine from.
 * @param[lifecycleState] The [Lifecycle.State] to launch the coroutine.
 * @param[block] Lambda to handle the state emissions.
 */
public fun <State> StateOwner<State>.collectState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (State) -> Unit,
): Job = state.lifecycleCollect(lifecycleOwner, lifecycleState, block)

/**
 * Collect emissions of a selected property of [State].
 *
 * @param[State] Type of collected state.
 * @param[T] Type of the selected property.
 * @param[lifecycleOwner] [LifecycleOwner] to launch coroutine from.
 * @param[selector] Lambda to select a property of [State] to collect.
 * @param[lifecycleState] The [Lifecycle.State] to launch the coroutine.
 * @param[block] Lambda to handle the selected property emissions.
 */
public fun <State, T> StateOwner<State>.collectState(
    lifecycleOwner: LifecycleOwner,
    selector: (State) -> T,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (T) -> Unit,
): Job = state.map(selector).lifecycleCollect(lifecycleOwner, lifecycleState, block)

/**
 * Launch a coroutine and repeat the [Flow.collect] on the given [lifecycleState].
 *
 * @see LifecycleOwner.repeatOnLifecycle
 */
private fun <T> Flow<T>.lifecycleCollect(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (T) -> Unit,
): Job = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(lifecycleState) {
        collect(block)
    }
}
