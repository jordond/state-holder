package dev.jordond.stateholder.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.jordond.stateholder.demo.AppModel.Event
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
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Button(onClick = { model.decrement() }) {
                    Text("Decrement")
                }

                Text("Count: ${state.count}", style = MaterialTheme.typography.headlineMedium)

                Button(onClick = { model.increment() }) {
                    Text("Increment")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { model.triggerEvent() }) {
                    Text("Trigger Event")
                }
            }
        }
    }
}