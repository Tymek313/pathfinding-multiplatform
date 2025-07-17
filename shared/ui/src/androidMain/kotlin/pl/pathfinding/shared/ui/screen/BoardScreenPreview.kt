package pl.pathfinding.shared.ui.screen

import androidx.compose.runtime.Composable
import pl.pathfinding.shared.ui.preview.DesktopPreview
import pl.pathfinding.shared.ui.preview.DesktopWindowSizeClass
import pl.pathfinding.shared.ui.preview.PhonePreview
import pl.pathfinding.shared.ui.preview.PhoneWindowSizeClass
import pl.pathfinding.shared.ui.theme.PathfindingBackground
import pl.pathfinding.shared.ui.theme.PathfindingTheme

@PhonePreview
@Composable
private fun BoardScreenPhonePreview() {
    PathfindingTheme {
        PathfindingBackground {
            BoardScreen(PhoneWindowSizeClass)
        }
    }
}

@DesktopPreview
@Composable
private fun BoardScreenDesktopPreview() {
    PathfindingTheme {
        PathfindingBackground {
            BoardScreen(DesktopWindowSizeClass)
        }
    }
}
