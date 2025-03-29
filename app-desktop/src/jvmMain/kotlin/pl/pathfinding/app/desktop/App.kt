package pl.pathfinding.app.desktop

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.stringResource
import pathfinding.shared.generated.resources.Res
import pathfinding.shared.generated.resources.graph_pathfinding
import pl.pathfinding.shared.ui.screen.BoardScreen
import pl.pathfinding.shared.ui.theme.PathfindingBackground
import pl.pathfinding.shared.ui.theme.PathfindingTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1200.dp, height = 900.dp, position = WindowPosition.Aligned(Alignment.Center)),
        title = stringResource(Res.string.graph_pathfinding)
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