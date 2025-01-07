package dev.stateholder.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.stateholder.EventHolder
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
public fun <Event> HandleEvents(
    holder: EventHolder<Event>,
    shouldHandle: (Event) -> Boolean = { true },
    onEvent: suspend (Event) -> Unit,
) {
    val events by holder.events.collectAsState()
    val handler = remember {
        { event: Event ->
            if (shouldHandle(event)) {
                holder.handle(event)
            }
        }
    }

    HandleEvents(events, handler, onEvent)
}

@Composable
public fun <Event> HandleEvents(
    events: List<Event>,
    handle: ((Event) -> Unit)? = null,
    onEvent: suspend (Event) -> Unit,
) {
    HandleEvents(events.toPersistentList(), handle, onEvent)
}

@Composable
public fun <Event> HandleEvents(
    events: PersistentList<Event>,
    handle: ((Event) -> Unit)? = null,
    onEvent: suspend (Event) -> Unit,
) {
    LaunchedEffect(events) {
        events.forEach { event ->
            onEvent(event)

            if (handle != null) {
                handle(event)
            }
        }
    }
}