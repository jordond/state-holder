package dev.stateholder

import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow

public interface EventHolder<Event> {
    public val events: StateFlow<PersistentList<Event>>

    public fun handle(event: Event)
}