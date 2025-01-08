package dev.stateholder.internal

import app.cash.turbine.test
import dev.stateholder.StateHolder
import dev.stateholder.provideState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultStateContainerTest {

    @Test
    fun shouldCreateStateContainerWithInitialState() = runTest {
        val container = DefaultStateContainer(provideState(123))

        container.state.test {
            awaitItem() shouldBe 123
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldUpdateState() = runTest {
        val container = DefaultStateContainer(provideState(0))

        container.state.test {
            awaitItem() shouldBe 0

            container.update { it + 1 }
            awaitItem() shouldBe 1

            container.update { it + 2 }
            awaitItem() shouldBe 3

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldNotEmitWhenStateIsUnchanged() = runTest {
        val container = DefaultStateContainer(provideState(0))

        container.state.test {
            awaitItem() shouldBe 0

            container.update { it }  // Same state
            expectNoEvents()

            container.update { it + 1 }  // Changed state
            awaitItem() shouldBe 1

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldAddSourceAndCollectUpdates() = runTest {
        val container = DefaultStateContainer(provideState(0))
        val source = MutableStateFlow(10)

        container.state.test {
            awaitItem() shouldBe 0

            val job = container.merge(source, this@runTest) { state, value ->
                state + value
            }

            awaitItem() shouldBe 10

            source.value = 20
            awaitItem() shouldBe 30

            job.cancel()
            source.value = 30
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldAddSourceWithStateHolder() = runTest {
        val container = DefaultStateContainer(provideState(0))
        val holder = TestStateHolder(10)

        container.state.test {
            awaitItem() shouldBe 0

            val job = container.merge(holder, this@runTest) { state1, state2 ->
                state1 + state2
            }

            awaitItem() shouldBe 10

            holder.updateState(20)
            awaitItem() shouldBe 30

            job.cancel()
            holder.updateState(30)
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldAddSourceWithOtherStateHolder() = runTest {
        val container1 = DefaultStateContainer(provideState(0))
        val container2 = DefaultStateContainer(provideState(10))

        container1.state.test {
            awaitItem() shouldBe 0

            val job = container1.merge(container2, this@runTest) { state1, state2 ->
                state1 + state2
            }

            awaitItem() shouldBe 10

            container2.update { it + 10 }
            awaitItem() shouldBe 30

            job.cancel()
            container2.update { it + 10 }
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    private class TestStateHolder(initialValue: Int) : StateHolder<Int> {
        private val _state = MutableStateFlow(initialValue)
        override val state: StateFlow<Int> = _state

        fun updateState(value: Int) {
            _state.value = value
        }
    }
}