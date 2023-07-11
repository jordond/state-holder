package co.onese.android.core.state.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stateholder.StateHolder
import kotlinx.coroutines.CoroutineScope

/**
 * A factory for creating a [StateHolder] that requires a [CoroutineScope] using Assisted inject.
 *
 * If your [StateHolder] requires a [CoroutineScope], first you must mark the class as
 * `@AssistedInject`, then the [CoroutineScope] as `@Assisted`, and finally create a factory that
 * implements [CoroutineStateContainerFactory] and returns the [StateHolder].
 *
 * Then in your [ViewModel] you can call [ViewModel.coroutineStateContainer] to get the the
 * injected [StateHolder] with the [CoroutineScope] set to [ViewModel.viewModelScope].
 *
 * **Note:** Make sure your `Factory` is annotated with `@AssistedFactory`!
 *
 * Create the [StateHolder] and it's [CoroutineStateContainerFactory]:
 *
 * ```
 * // Mark the constructor as @AssistedInject
 * class MyStateContainer @AssistedInject constructor(
 *     @Assisted coroutineScope: CoroutineScope,
 *     myRepo: MyRepo,
 * ) : StateContainer<MyState> {
 *
 *     init {
 *         // We need the CoroutineScope so we can launch a flow and update the state
 *         addSource(myRepo.someFlowOfData(), coroutineScope) { state, data ->
 *             state.copy(someData = data)
 *         }
 *     }
 *
 *     // Implement the factory with the AssistedFactory annotation.
 *     @AssistedFactory
 *     interface Factory : CoroutineStateContainerFactory<MyStateContainer> {
 *         override fun create(coroutineScope: CoroutineScope): MyStateContainer
 *     }
 * }
 * ```
 *
 * In your [ViewModel] use [ViewModel.coroutineStateContainer] to get the [StateHolder]:
 *
 * ```
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     stateContainerFactory: MyStateContainer.Factory,
 * ) : ViewModel(), StateOwner<MyViewModel.State> {
 *
 *     private val container = coroutineStateContainer(stateContainerFactory)
 *     override val state by container
 * }
 * ```
 */
public interface CoroutineStateContainerFactory<T : StateHolder<*>> {

    public fun create(coroutineScope: CoroutineScope): T
}

/**
 * Creates a [StateHolder] of type [T] using the [CoroutineStateContainerFactory] and sets the
 * [CoroutineScope] to [ViewModel.viewModelScope].
 *
 * @param[T] The type of [StateHolder] to create.
 * @param[factory] The [CoroutineStateContainerFactory] to use to create the [StateHolder].
 * @return The created [StateHolder].
 */
public fun <T : StateHolder<*>> ViewModel.coroutineStateContainer(
    factory: CoroutineStateContainerFactory<T>,
): T {
    return factory.create(viewModelScope)
}

/**
 * Creates a [StateHolder] of type [T] using the [CoroutineStateContainerFactory] and sets the
 * [CoroutineScope].
 *
 * @param[T] The type of [StateHolder] to create.
 * @param[factory] The [CoroutineStateContainerFactory] to use to create the [StateHolder].
 * @return The created [StateHolder].
 */
public fun <T : StateHolder<*>> CoroutineScope.coroutineStateContainer(
    factory: CoroutineStateContainerFactory<T>,
): T {
    return factory.create(this)
}
