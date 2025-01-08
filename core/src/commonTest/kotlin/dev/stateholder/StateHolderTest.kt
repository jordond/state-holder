package dev.stateholder

import app.cash.turbine.test
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class StateHolderTest {
    @Test
    fun shouldCreateAStateHolder() = runTest {
        val container = stateContainer(true)
        container.asStateHolder().state.test {
            awaitItem().shouldBeTrue()

            container.update { false }
            awaitItem().shouldBeFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }
}