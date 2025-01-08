package dev.stateholder

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class StateProviderTest {
    @Test
    fun shouldCreateStateProviderWithInitialValue() = runTest {
        val provider = provideState(123)

        provider.provide() shouldBe 123
    }

    @Test
    fun shouldCreateStateProviderWithLazyInitialization() = runTest {
        var initializerCalled = false
        val provider = provideState {
            initializerCalled = true
            456
        }

        initializerCalled shouldBe false
        provider.provide() shouldBe 456
        initializerCalled shouldBe true
    }

    @Test
    fun shouldCreateAStateProviderFromAnyType() = runTest {
        val provider = "Foo".asStateProvider()
        provider.provide() shouldBe "Foo"
    }
}