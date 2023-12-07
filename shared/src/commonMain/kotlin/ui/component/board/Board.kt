package ui.component.board

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
    Column(Modifier.boardPointerInput(state)) {
        (0..<state.nodeCountY).forEach { y ->
            Row {
                (0..<state.nodeCountX).forEach { x ->
                    Box(modifier = Modifier.size(state.nodeDisplaySizeDp).background(state.getNodeStateAtPosition(x, y).color).border(1.dp, Color.Black))
                }
            }
        }
    }
}

private fun Modifier.boardPointerInput(state: BoardState): Modifier {
    return pointerInput(Unit) {
        detectDragGestures(
            onDragStart = state::onDragStart,
            onDragEnd = state::onDragEnd,
            onDrag = { change, _ -> if (state.onDrag(change.position)) change.consume() }
        )
    }.pointerInput(Unit) {
        detectTapGestures(state::onNodeClick)
    }
}