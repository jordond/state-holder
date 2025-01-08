package dev.stateholder

import app.cash.turbine.test
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class StateOwnerTest {
    @Test
    fun shouldCreateAStateOwner() = runTest {
        val holder = stateContainer(true)
        holder.asStateOwner().state.test {
            awaitItem().shouldBeTrue()

            holder.update { false }
            awaitItem().shouldBeFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }
}