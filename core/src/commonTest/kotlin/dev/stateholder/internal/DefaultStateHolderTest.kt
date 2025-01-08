package dev.stateholder.internal

import app.cash.turbine.test
import dev.stateholder.StateOwner
import dev.stateholder.provideState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultStateHolderTest {

    @Test
    fun shouldCreateStateHolderWithInitialState() = runTest {
        val holder = DefaultStateHolder(provideState(123))

        holder.state.test {
            awaitItem() shouldBe 123
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldUpdateState() = runTest {
        val holder = DefaultStateHolder(provideState(0))

        holder.state.test {
            awaitItem() shouldBe 0

            holder.update { it + 1 }
            awaitItem() shouldBe 1

            holder.update { it + 2 }
            awaitItem() shouldBe 3

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldNotEmitWhenStateIsUnchanged() = runTest {
        val holder = DefaultStateHolder(provideState(0))

        holder.state.test {
            awaitItem() shouldBe 0

            holder.update { it }  // Same state
            expectNoEvents()

            holder.update { it + 1 }  // Changed state
            awaitItem() shouldBe 1

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldAddSourceAndCollectUpdates() = runTest {
        val holder = DefaultStateHolder(provideState(0))
        val source = MutableStateFlow(10)

        holder.state.test {
            awaitItem() shouldBe 0

            val job = holder.addSource(source, this@runTest) { state, value ->
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
    fun shouldAddSourceWithStateOwner() = runTest {
        val holder = DefaultStateHolder(provideState(0))
        val owner = TestStateOwner(10)

        holder.state.test {
            awaitItem() shouldBe 0

            val job = holder.addSource(owner, this@runTest) { state1, state2 ->
                state1 + state2
            }

            awaitItem() shouldBe 10

            owner.updateState(20)
            awaitItem() shouldBe 30

            job.cancel()
            owner.updateState(30)
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldAddSourceWithOtherStateHolder() = runTest {
        val holder1 = DefaultStateHolder(provideState(0))
        val holder2 = DefaultStateHolder(provideState(10))

        holder1.state.test {
            awaitItem() shouldBe 0

            val job = holder1.addSource(holder2, this@runTest) { state1, state2 ->
                state1 + state2
            }

            awaitItem() shouldBe 10

            holder2.update { it + 10 }
            awaitItem() shouldBe 30

            job.cancel()
            holder2.update { it + 10 }
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    private class TestStateOwner(initialValue: Int) : StateOwner<Int> {
        private val _state = MutableStateFlow(initialValue)
        override val state: StateFlow<Int> = _state

        fun updateState(value: Int) {
            _state.value = value
        }
    }
}