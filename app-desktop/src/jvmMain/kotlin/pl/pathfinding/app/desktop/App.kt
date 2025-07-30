package pl.pathfinding.app.desktop

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import java.awt.Dimension
import java.io.PrintWriter
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import pathfinding.shared.ui.generated.resources.Res
import pathfinding.shared.ui.generated.resources.graph_pathfinding
import pl.pathfinding.shared.ui.screen.BoardScreen
import pl.pathfinding.shared.ui.theme.PathfindingBackground
import pl.pathfinding.shared.ui.theme.PathfindingTheme

fun main() {
    registerGlobalExceptionHandler()
    singleWindowApplication(
        state = WindowState(width = 1200.dp, height = 700.dp, position = WindowPosition.Aligned(Alignment.Center)),
        title = runBlocking { getString(Res.string.graph_pathfinding) }
    ) {
        val density = LocalDensity.current
        LaunchedEffect(Unit) {
            window.minimumSize = density.run { Dimension(500.dp.roundToPx(), 500.dp.roundToPx()) }
        }
        App()
    }
}

private fun registerGlobalExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        // For debugging release builds
        PrintWriter("error-log.txt").use(throwable::printStackTrace)
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
