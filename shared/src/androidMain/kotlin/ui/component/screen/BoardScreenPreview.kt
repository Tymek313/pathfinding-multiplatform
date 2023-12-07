package ui.component.screen

import androidx.compose.runtime.Composable
import ui.preview.DesktopPreview
import ui.preview.DesktopWindowSizeClass
import ui.preview.PhonePreview
import ui.preview.PhoneWindowSizeClass
import ui.screen.BoardScreen
import ui.theme.PathfindingBackground
import ui.theme.PathfindingTheme

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
