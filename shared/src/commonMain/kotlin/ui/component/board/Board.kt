package ui.component.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

private val maxNodeSize = 20.dp

@Composable
fun Board(state: BoardState, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier) {
        val nodeSize = remember(maxWidth, maxHeight) {
            min(if (maxWidth < maxHeight) maxWidth / state.nodeCountX else maxHeight / state.nodeCountY, maxNodeSize)
        }

        Column(Modifier.boardPointerInput(nodeSize, state)) {
            (0..<state.nodeCountY).forEach { y ->
                Row {
                    (0..<state.nodeCountX).forEach { x ->
                        Box(
                            Modifier
                                .size(nodeSize)
                                .background(state.getNodeColorAtPosition(x, y))
                                .border(1.dp, Color.Black)
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.boardPointerInput(nodeSize: Dp, state: BoardState): Modifier {
    return this then pointerInput(nodeSize) {
        detectDragGestures(
            onDragStart = { offset -> state.onDragStart(nodeSize, offset) },
            onDragEnd = state::onDragEnd,
            onDrag = { change, _ -> if (state.onDrag(nodeSize, change.position)) change.consume() }
        )
    }.pointerInput(nodeSize) {
        detectTapGestures { offset -> state.onNodeClick(nodeSize, offset) }
    }
}