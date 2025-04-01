package pl.pathfinding.shared.ui.component.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp

@Composable
internal fun Board(state: BoardState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .boardPointerInput(state)
            .onSizeChanged { state.boardSizeInPixels = it.width }
    ) {
        (0..<state.nodeCount).forEach { y ->
            Row(modifier = Modifier.weight(1f)) {
                (0..<state.nodeCount).forEach { x ->
                    Node(state, x, y, Modifier.weight(1f))
                }
            }
        }
    }
}

// New composition restart point
@Composable
private fun Node(state: BoardState, x: Int, y: Int, modifier: Modifier) {
    Box(
        modifier
            .fillMaxHeight()
            .background(state.getNodeColorAt(x, y).value)
            .border(1.dp, Color.Black)
    )
}

private fun Modifier.boardPointerInput(state: BoardState): Modifier {
    return this then pointerInput(Unit) {
        detectDragGestures(
            onDragStart = state::onDragStart,
            onDragEnd = state::onDragEnd,
            onDrag = { change, _ ->
                if (state.onDrag(change.position)) {
                    change.consume()
                }
            }
        )
    }.pointerInput(Unit) {
        detectTapGestures(onTap = state::onNodeClick)
    }
}