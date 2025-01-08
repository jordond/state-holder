package dev.jordond.stateholder.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.jordond.stateholder.demo.AppAction.Decrement
import dev.jordond.stateholder.demo.AppAction.Increment
import dev.jordond.stateholder.demo.AppAction.SetCount
import dev.jordond.stateholder.demo.AppAction.TriggerEvent
import dev.jordond.stateholder.demo.AppModel.Event
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.dispatcher.rememberRelayOf
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState

class AppScreen : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel { AppModel() }
        val state by model.collectAsState()

        val snackbar = remember { SnackbarHostState() }

        HandleEvents(
            holder = model,
            shouldHandle = { // event ->
                // Here we can filter events to handle only specific ones, in case they are shared
                // between multiple screens
                true
            }
        ) { event ->
            when (event) {
                is Event.Toast -> snackbar.showSnackbar(message = "Hello World!")
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
        ) { innerPadding ->
            AppContent(
                count = state.count,
                dispatcher = rememberDispatcher(debounce = 100L) { action ->
                    when (action) {
                        is Decrement -> model.decrement()
                        is Increment -> model.increment()
                        is SetCount -> model.setCount(action.count)
                        is TriggerEvent -> model.triggerEvent()
                    }
                },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

sealed interface AppAction {
    data object Increment : AppAction
    data object Decrement : AppAction
    data object TriggerEvent : AppAction
    data class SetCount(val count: Int) : AppAction
}

@Composable
private fun AppContent(
    count: Int,
    dispatcher: Dispatcher<AppAction>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Button(onClick = dispatcher.rememberRelay(Decrement)) {
            Text("Decrement")
        }

        Text("Count: $count", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = dispatcher.rememberRelay(Increment)) {
            Text("Increment")
        }

        Spacer(modifier = Modifier.height(8.dp))
        CustomCount(
            initialCount = count,
            updateCount = dispatcher.rememberRelayOf(::SetCount),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = dispatcher.rememberRelay(TriggerEvent)) {
            Text("Trigger Event")
        }
    }
}

@Composable
fun CustomCount(
    initialCount: Int,
    updateCount: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var count by remember { mutableIntStateOf(initialCount) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            value = count.toString(),
            onValueChange = { count = it.toIntOrNull() ?: 0 },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { updateCount(count) }),
            modifier = modifier,
        )

        TextButton(
            enabled = count != initialCount,
            onClick = { updateCount(count) },
        ) {
            Text("Save")
        }
    }
}