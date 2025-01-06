import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.jordond.stateholder.demo.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Stateholder demo",
    ) {
        App()
    }
}
