package dev.stateholder.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import dev.stateholder.StateOwner
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
public expect fun <T> StateOwner<T>.collectAsState(
    context: CoroutineContext = EmptyCoroutineContext,
): State<T>
