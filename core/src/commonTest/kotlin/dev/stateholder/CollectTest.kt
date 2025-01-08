package dev.stateholder

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CollectTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldCollectStateWithDefaultParameters() = runTest(timeout = 3.seconds) {
        val holder = TestStateOwner(0)
        val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        holder.collectState(lifecycleOwner) { value ->
            value shouldBe 0
        }

    }

    @Test
    fun shouldCollectStateWithCustomParameters() = runTest(timeout = 3.seconds) {
        val holder = TestStateOwner(10)
        val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        val customContext = TestCoroutineContext()

        holder.collectState(
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.RESUMED,
            context = customContext
        ) { value ->
            value shouldBe 10
            customContext.wasUsed shouldBe true
        }
    }

    @Test
    fun shouldCollectSelectedStateProperty() = runTest(timeout = 3.seconds) {
        val holder = TestStateOwner(TestData("test", 123))
        val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        holder.collectState(
            lifecycleOwner = lifecycleOwner,
            selector = { it.text }
        ) { value ->
            value shouldBe "test"
        }
    }

    @Test
    fun shouldCollectSelectedStatePropertyWithCustomParameters() = runTest(timeout = 3.seconds) {
        val holder = TestStateOwner(TestData("test", 123))
        val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        val customContext = TestCoroutineContext()

        holder.collectState(
            lifecycleOwner = lifecycleOwner,
            selector = { it.text },
            minActiveState = Lifecycle.State.RESUMED,
            context = customContext
        ) { value ->
            value shouldBe "test"
            customContext.wasUsed shouldBe true
        }
    }

    private data class TestData(val text: String, val number: Int)

    private class TestStateOwner<T>(initialValue: T) : StateOwner<T> {
        val _state = MutableStateFlow(initialValue)
        override val state: StateFlow<T> = _state
    }

    private class TestLifecycle(override val currentState: State) : Lifecycle() {
        override fun addObserver(observer: LifecycleObserver) {}
        override fun removeObserver(observer: LifecycleObserver) {}
    }

    private class TestLifecycleOwner(currentState: Lifecycle.State) : LifecycleOwner {
        private val _lifecycle = TestLifecycle(currentState)
        override val lifecycle: Lifecycle = _lifecycle
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