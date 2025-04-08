package pl.pathfinding.shared.ui.component.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
internal fun Board(state: BoardState, modifier: Modifier = Modifier) {
    val nodeCount = remember(state.nodeCount) { state.nodeCount * state.nodeCount }
    BoardLayout(
        nodeCount = state.nodeCount,
        modifier = modifier
            .boardPointerInput(state)
            .onSizeChanged { state.boardSizeInPixels = it.width }
    ) {
        repeat(nodeCount) { nodeIndex ->
            Node(state, nodeIndex)
        }
    }
}

private val MAX_NODE_SIZE = 30.dp

@Composable
private fun BoardLayout(nodeCount: Int, modifier: Modifier, content: @Composable () -> Unit) {
    Layout(content, modifier) { measurables, constraints ->
        val boardMaxSize = min(constraints.maxWidth, constraints.maxHeight)
        val nodeSize = (boardMaxSize / nodeCount).coerceAtMost(MAX_NODE_SIZE.roundToPx())
        val placeables = measurables.map { it.measure(Constraints.fixed(nodeSize, nodeSize)) }
        val boardSize = nodeSize * nodeCount

        layout(boardSize, boardSize) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(index % nodeCount * nodeSize, index / nodeCount * nodeSize)
            }
        }
    }
}

@Composable
private fun Node(state: BoardState, nodeIndex: Int, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(state.nodeColors[nodeIndex].value)
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