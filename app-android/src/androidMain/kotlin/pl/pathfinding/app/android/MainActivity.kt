package pl.pathfinding.app.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import pl.pathfinding.shared.ui.screen.BoardScreen
import pl.pathfinding.shared.ui.theme.PathfindingBackground
import pl.pathfinding.shared.ui.theme.PathfindingTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PathfindingTheme {
                PathfindingBackground {
                    BoardScreen(calculateWindowSizeClass(this))
                }
            }
        }
    }
}