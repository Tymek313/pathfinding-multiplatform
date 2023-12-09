package pl.pathfinding

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.resource.StringRes
import ui.screen.BoardScreen
import ui.theme.PathfindingBackground
import ui.theme.PathfindingTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1200.dp, height = 900.dp, position = WindowPosition.Aligned(Alignment.Center)),
        title = StringRes.graphPathfinding
    ) {
        App()
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun App() {
    PathfindingTheme {
        PathfindingBackground {
            BoardScreen(calculateWindowSizeClass())
        }
    }
}