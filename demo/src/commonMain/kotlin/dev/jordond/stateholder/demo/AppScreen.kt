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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.stateholder.extensions.collectAsState

class AppScreen : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel { AppModel() }
        val state by model.collectAsState()
        Scaffold { innerPadding ->
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