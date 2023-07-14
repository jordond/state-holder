package dev.stateholder.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.stateholder.StateOwner
import kotlin.coroutines.CoroutineContext
import com.arkivanov.essenty.lifecycle.Lifecycle as EssentyLifecycle

@Composable
public actual fun <T> StateOwner<T>.collectAsState(
    minActiveState: EssentyLifecycle.State,
    context: CoroutineContext,
): State<T> {
    return state.collectAsStateWithLifecycle(
        minActiveState = minActiveState.toAndroidLifecycleState(),
        context = context,
    )
}

private fun EssentyLifecycle.State.toAndroidLifecycleState(): Lifecycle.State = when (this) {
    EssentyLifecycle.State.INITIALIZED -> Lifecycle.State.INITIALIZED
    EssentyLifecycle.State.CREATED -> Lifecycle.State.CREATED
    EssentyLifecycle.State.STARTED -> Lifecycle.State.STARTED
    EssentyLifecycle.State.RESUMED -> Lifecycle.State.RESUMED
    EssentyLifecycle.State.DESTROYED -> Lifecycle.State.DESTROYED
}