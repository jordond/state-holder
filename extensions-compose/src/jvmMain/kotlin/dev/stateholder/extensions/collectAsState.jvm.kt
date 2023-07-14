package dev.stateholder.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.arkivanov.essenty.lifecycle.Lifecycle
import dev.stateholder.StateOwner
import kotlin.coroutines.CoroutineContext

@Composable
public actual fun <T> StateOwner<T>.collectAsState(
    minActiveState: Lifecycle.State,
    context: CoroutineContext,
): State<T> {
    return state.collectAsState(context = context)
}