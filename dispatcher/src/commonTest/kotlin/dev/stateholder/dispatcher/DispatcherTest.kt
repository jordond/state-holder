package dev.stateholder.dispatcher

import dev.stateholder.dispatcher.DispatcherTest.TestAction.FiveParams
import dev.stateholder.dispatcher.DispatcherTest.TestAction.FourParams
import dev.stateholder.dispatcher.DispatcherTest.TestAction.SingleParam
import dev.stateholder.dispatcher.DispatcherTest.TestAction.SixParams
import dev.stateholder.dispatcher.DispatcherTest.TestAction.ThreeParams
import dev.stateholder.dispatcher.DispatcherTest.TestAction.TwoParams
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DispatcherTest {
    @Test
    fun shouldDispatchAction() = runTest {
        var dispatchedAction: String? = null
        val dispatcher = Dispatcher<String> { action ->
            dispatchedAction = action
        }

        dispatcher.dispatch("test")
        dispatchedAction shouldBe "test"
    }

    @Test
    fun shouldInvokeOperatorDispatchAction() = runTest {
        var dispatchedAction: String? = null
        val dispatcher = Dispatcher<String> { action ->
            dispatchedAction = action
        }

        dispatcher("test")
        dispatchedAction shouldBe "test"
    }

    @Test
    fun shouldCreateRelayFunction() = runTest {
        var dispatchedAction: String? = null
        val dispatcher = Dispatcher<String> { action ->
            dispatchedAction = action
        }

        val relay = dispatcher.relay("test")
        relay()
        dispatchedAction shouldBe "test"
    }

    @Test
    fun shouldCreateSingleParameterRelay() = runTest {
        var dispatchedAction: TestAction? = null
        val dispatcher = Dispatcher<TestAction> { action ->
            dispatchedAction = action
        }

        val relay = dispatcher.relayOf(::SingleParam)
        relay("test")
        dispatchedAction shouldBe SingleParam("test")
    }

    @Test
    fun shouldCreateTwoParameterRelay() = runTest {
        var dispatchedAction: TestAction? = null
        val dispatcher = Dispatcher<TestAction> { action ->
            dispatchedAction = action
        }

        val relay = dispatcher.relayOf(::TwoParams)
        relay("test", 42)
        dispatchedAction shouldBe TwoParams("test", 42)
    }

    @Test
    fun shouldCreateThreeParameterRelay() = runTest {
        var dispatchedAction: TestAction? = null
        val dispatcher = Dispatcher<TestAction> { action ->
            dispatchedAction = action
        }

        val relay = dispatcher.relayOf(::ThreeParams)
        relay("test", 42, true)
        dispatchedAction shouldBe ThreeParams("test", 42, true)
    }

    @Test
    fun shouldCreateFourParameterRelay() = runTest {
        var dispatchedAction: TestAction? = null
        val dispatcher = Dispatcher<TestAction> { action ->
            dispatchedAction = action
        }

        val relay = dispatcher.relayOf(::FourParams)
        relay("test", 42, true, 3.14)
        dispatchedAction shouldBe FourParams("test", 42, true, 3.14)
    }

    @Test
    fun shouldCreateFiveParameterRelay() = runTest {
        var dispatchedAction: TestAction? = null
        val dispatcher = Dispatcher<TestAction> { action ->
            dispatchedAction = action
        }

        val relay = dispatcher.relayOf(::FiveParams)
        relay("test", 42, true, 3.14, 'A')
        dispatchedAction shouldBe FiveParams("test", 42, true, 3.14, 'A')
    }

    @Test
    fun shouldCreateSixParameterRelay() = runTest {
        var dispatchedAction: TestAction? = null
        val dispatcher = Dispatcher<TestAction> { action ->
            dispatchedAction = action
        }

        val relay = dispatcher.relayOf(::SixParams)
        relay("test", 42, true, 3.14, 'A', "extra")
        dispatchedAction shouldBe SixParams("test", 42, true, 3.14, 'A', "extra")
    }

    @Test
    fun shouldNotCleanupRecentEntries() = runTest {
        var dispatchedCount = 0
        val dispatcher = DebounceDispatcher<String>(debounce = 100L) { _ ->
            dispatchedCount++
        }

        // First dispatch
        dispatcher.dispatch("test1")
        dispatchedCount shouldBe 1

        // Wait less than debounce period
        advanceTimeBy(50)

        // Try same action - should be debounced
        dispatcher.dispatch("test1")
        dispatchedCount shouldBe 1

        // Different action should still work
        dispatcher.dispatch("test2")
        dispatchedCount shouldBe 2
    }

    private sealed interface TestAction {
        data class SingleParam(val text: String) : TestAction
        data class TwoParams(val text: String, val number: Int) : TestAction
        data class ThreeParams(val text: String, val number: Int, val flag: Boolean) : TestAction
        data class FourParams(
            val text: String,
            val number: Int,
            val flag: Boolean,
            val decimal: Double,
        ) : TestAction

        data class FiveParams(
            val text: String,
            val number: Int,
            val flag: Boolean,
            val decimal: Double,
            val char: Char,
        ) : TestAction

        data class SixParams(
            val text: String,
            val number: Int,
            val flag: Boolean,
            val decimal: Double,
            val char: Char,
            val extra: String,
        ) : TestAction
    }
}