package pl.pathfinding.appandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import ui.screen.BoardScreen
import ui.theme.PathfindingBackground
import ui.theme.PathfindingTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PathfindingTheme {
                PathfindingBackground {
                    BoardScreen(calculateWindowSizeClass())
                }
            }
        }
    }
}