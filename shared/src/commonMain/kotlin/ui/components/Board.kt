package ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun Board(state: BoardState) {
    Column(
        Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { state.onDragStart(pointerPosition = it) },
                onDragEnd = { state.onDragEnd() },
                onDrag = { change, _ -> state.onDrag(pointerPosition = change.position) }
            )
        }.pointerInput(Unit) {
            detectTapGestures { state.onFieldClick(pointerPosition = it) }
        }
    ) {
        (0..<state.sizeY).forEach { y ->
            Row {
                (0..<state.sizeX).forEach { x ->
                    Box(modifier = Modifier.size(state.fieldSizeDp).background(state.getFieldStateAtPosition(x, y).color).border(1.dp, Color.Black))
                }
            }
        }
    }
}

// Throws an exception on desktop https://github.com/JetBrains/compose-multiplatform/issues/3975
//private val BottomLineShape = GenericShape { size, _ ->
//    moveTo(0f, size.height)
//    lineTo(size.width, size.height)
//}

@Composable
@Preview
private fun BoardPreview() {
    Board(
        BoardState(
            startPosition = BoardState.Position(x = 0, y = 0),
            endPosition = BoardState.Position(x = 19, y = 19),
            sizeX = 20,
            sizeY = 20
        )
    )
}