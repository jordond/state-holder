package dev.stateholder.dispatcher

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class DebounceDispatcherTest {

    @Test
    fun shouldDispatchFirstActionImmediately() = runTest {
        var dispatchCount = 0
        val dispatcher = DebounceDispatcher<String>(
            debounce = 100,
            block = { dispatchCount++ }
        )

        dispatcher.dispatch("test")
        dispatchCount shouldBe 1
    }

    @Test
    fun shouldDebounceRepeatedActions() = runTest {
        var dispatchCount = 0
        val dispatcher = DebounceDispatcher<String>(
            debounce = 100,
            block = { dispatchCount++ }
        )

        dispatcher.dispatch("test")
        dispatcher.dispatch("test")
        dispatcher.dispatch("test")

        dispatchCount shouldBe 1
    }

    @Test
    fun shouldDispatchDifferentActionsImmediately() = runTest {
        var dispatchCount = 0
        val dispatcher = DebounceDispatcher<String>(
            debounce = 100,
            block = { dispatchCount++ }
        )

        dispatcher.dispatch("test1")
        dispatcher.dispatch("test2")
        dispatcher.dispatch("test3")

        dispatchCount shouldBe 3
    }

    @Test
    fun shouldDispatchExcludedActionsImmediately() = runTest {
        var dispatchCount = 0
        val dispatcher = DebounceDispatcher<String>(
            debounce = 100,
            exclude = listOf("excluded"),
            block = { dispatchCount++ }
        )

        dispatcher.dispatch("excluded")
        dispatcher.dispatch("excluded")
        dispatcher.dispatch("excluded")

        dispatchCount shouldBe 3
    }

    @Test
    fun shouldDispatchAfterDebounceTime() = runTest {
        var lastDispatchedAction: String? = null
        val dispatcher = DebounceDispatcher<String>(
            debounce = 50,
            block = { lastDispatchedAction = it }
        )

        dispatcher.dispatch("first")
        lastDispatchedAction shouldBe "first"

        // Simulate time passing
        advanceTimeBy(100.milliseconds)

        dispatcher.dispatch("second")
        lastDispatchedAction shouldBe "second"
    }

    @Test
    fun shouldUseDefaultDebounceTime() = runTest {
        var dispatchCount = 0
        val dispatcher = DebounceDispatcher<String>(
            block = { dispatchCount++ }
        )

        dispatcher.dispatch("test")
        dispatcher.dispatch("test")

        dispatchCount shouldBe 1
    }

    @Test
    fun shouldCreateWithFactoryFunction() = runTest {
        var dispatchCount = 0
        val dispatcher = Dispatcher<String>(
            debounce = 100,
            block = { dispatchCount++ }
        )

        dispatcher.dispatch("test")
        dispatcher.dispatch("test")

        dispatchCount shouldBe 1
    }
}