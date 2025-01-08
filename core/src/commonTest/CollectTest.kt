package dev.stateholder

import app.cash.turbine.test
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test

class CollectTest {

    @Test
    fun shouldCollectStateWithDefaultParameters() = runTest {
        val holder = TestStateOwner(0)
        val lifecycleOwner = TestLifecycleOwner(this)
        var collected = 0

        holder.collectState(lifecycleOwner) { value ->
            collected = value
        }

        collected shouldBe 0
        lifecycleOwner.minActiveState shouldBe Lifecycle.State.STARTED
    }

    @Test
    fun shouldCollectStateWithCustomParameters() = runTest {
        val holder = TestStateOwner(0)
        val lifecycleOwner = TestLifecycleOwner(this)
        var collected = 0
        val customContext = TestCoroutineContext()

        holder.collectState(
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.RESUMED,
            context = customContext
        ) { value ->
            collected = value
        }

        collected shouldBe 0
        lifecycleOwner.minActiveState shouldBe Lifecycle.State.RESUMED
        customContext.wasUsed shouldBe true
    }

    @Test
    fun shouldCollectSelectedStateProperty() = runTest {
        val holder = TestStateOwner(TestData("test", 123))
        val lifecycleOwner = TestLifecycleOwner(this)
        var collected = ""

        holder.collectState(
            lifecycleOwner = lifecycleOwner,
            selector = { it.text }
        ) { value ->
            collected = value
        }

        collected shouldBe "test"
        lifecycleOwner.minActiveState shouldBe Lifecycle.State.STARTED
    }

    @Test
    fun shouldCollectSelectedStatePropertyWithCustomParameters() = runTest {
        val holder = TestStateOwner(TestData("test", 123))
        val lifecycleOwner = TestLifecycleOwner(this)
        var collected = ""
        val customContext = TestCoroutineContext()

        holder.collectState(
            lifecycleOwner = lifecycleOwner,
            selector = { it.text },
            minActiveState = Lifecycle.State.RESUMED,
            context = customContext
        ) { value ->
            collected = value
        }

        collected shouldBe "test"
        lifecycleOwner.minActiveState shouldBe Lifecycle.State.RESUMED
        customContext.wasUsed shouldBe true
    }

    private data class TestData(val text: String, val number: Int)

    private class TestStateOwner<T>(initialValue: T) : StateOwner<T> {
        private val _state = MutableStateFlow(initialValue)
        override val state: StateFlow<T> = _state
    }

    private class TestLifecycleOwner(
        private val scope: TestScope
    ) : LifecycleOwner {
        var minActiveState: Lifecycle.State? = null
            private set
        
        override val lifecycleScope = scope

        fun repeatOnLifecycle(
            state: Lifecycle.State,
            block: suspend () -> Unit
        ): Job {
            minActiveState = state
            return scope.launch { block() }
        }
    }

    private class TestCoroutineContext : CoroutineContext {
        var wasUsed = false
            private set

        override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R {
            wasUsed = true
            return initial
        }

        override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? {
            wasUsed = true
            return null
        }

        override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext {
            wasUsed = true
            return EmptyCoroutineContext
        }
    }
}
