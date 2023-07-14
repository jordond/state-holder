package dev.stateholder.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.stateholder.StateOwner
import kotlin.coroutines.CoroutineContext

@Composable
public actual fun <T> StateOwner<T>.collectAsState(context: CoroutineContext): State<T> {
    return state.collectAsStateWithLifecycle(context = context)
}