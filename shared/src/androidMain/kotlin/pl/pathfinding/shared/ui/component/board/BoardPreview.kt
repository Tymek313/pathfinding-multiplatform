package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun BoardPreview() {
    Board(rememberBoardState(size = 20))
}