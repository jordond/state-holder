package dev.stateholder

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class StateContainerTest {
    @Test
    fun shouldCreateStateContainerWithInitialState() = runTest {
        val container = stateContainer(123)

        container.state.test {
            awaitItem() shouldBe 123
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldCreateStateContainerWithStateProvider() = runTest {
        val provider = provideState(456)
        val container = stateContainer(provider)

        container.state.test {
            awaitItem() shouldBe 456
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun shouldImplementGetValue() = runTest {
        val container = stateContainer(789)
        container.getValue(
            stateHolder = container.asStateOwner(),
            property = StateContainer<Int>::state,
        ).value shouldBe 789
    }

    @Test
    fun shouldCombineFlowToState() = runTest {
        val container = stateContainer(100)
        val flow = MutableStateFlow(50)

        val collectJob = flow.combineWithState(container, this) { state, value ->
            state + value
        }

        container.state.test {
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