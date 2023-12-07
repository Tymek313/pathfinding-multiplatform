package ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun BoardPreview() {
    Board(rememberBoardState(sizeX = 20, sizeY = 30))
}