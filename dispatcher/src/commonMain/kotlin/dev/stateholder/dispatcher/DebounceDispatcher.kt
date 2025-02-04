package dev.stateholder.dispatcher

import kotlinx.datetime.Clock

@Suppress("FunctionName")
public fun <Action> DebounceDispatcher(
    debounce: Long = 100,
    exclude: (Action) -> Boolean = { false },
    block: (Action) -> Unit,
): Dispatcher<Action> = object : Dispatcher<Action> {
    private val lookup: HashMap<Action, Long> = hashMapOf()

    override fun dispatch(action: Action) {
        if (exclude(action)) {
            return block(action)
        }

        val currentTime = Clock.System.now().toEpochMilliseconds()

        lookup.entries.removeAll { (_, timestamp) ->
            currentTime - timestamp > debounce
        }

        val lastTime = lookup[action]
        if (lastTime == null || currentTime - lastTime > debounce) {
            lookup[action] = currentTime
            block(action)
        }
    }
}

public fun <Action> Dispatcher(
    debounce: Long,
    exclude: (Action) -> Boolean = { false },
    block: (Action) -> Unit,
): Dispatcher<Action> = DebounceDispatcher(debounce, exclude, block)