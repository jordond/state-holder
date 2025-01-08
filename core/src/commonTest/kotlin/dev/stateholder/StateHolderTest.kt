package dev.stateholder

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class StateHolderTest {
    @Test
    fun shouldCreateStateHolderWithInitialState() = runTest {
        val holder = stateContainer(123)

        holder.state.test {
            awaitItem() shouldBe 123
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldCreateStateHolderWithStateProvider() = runTest {
        val provider = provideState(456)
        val holder = stateContainer(provider)

        holder.state.test {
            awaitItem() shouldBe 456
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldImplementGetValue() = runTest {
        val holder = stateContainer(789)
        holder.getValue(
            stateOwner = holder.asStateOwner(),
            property = StateHolder<Int>::state,
        ).value shouldBe 789
    }

    @Test
    fun shouldCombineFlowToState() = runTest {
        val holder = stateContainer(100)
        val flow = MutableStateFlow(50)

        val collectJob = flow.combineWithState(holder, this) { state, value ->
            state + value
        }

        holder.state.test {
            awaitItem() shouldBe 100  // Initial state
            awaitItem() shouldBe 150  // After collect (100 + 50)

            flow.value = 75
            awaitItem() shouldBe 225  // After update (150 + 75)

            collectJob.cancel()
            flow.value = 25
            expectNoEvents()  // No updates after cancellation

            cancelAndIgnoreRemainingEvents()
        }
    }
}