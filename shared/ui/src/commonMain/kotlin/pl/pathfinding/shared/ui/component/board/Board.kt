package pl.pathfinding.shared.ui.component.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.pathfinding.shared.domain.node.NodeId
import kotlin.math.min

@Composable
internal fun Board(state: BoardState, modifier: Modifier = Modifier) {
    var boardWidthPx by remember { mutableIntStateOf(0) }

    BoardLayout(
        sizeInNodes = state.boardSize,
        modifier = modifier
            .boardPointerInput(state, boardWidthPx)
            .onSizeChanged { boardWidthPx = it.width }
    ) {
        repeat(state.boardSize * state.boardSize) { nodeIndex ->
            Node(state, nodeIndex)
        }
    }
}

private val MAX_NODE_SIZE = 30.dp

@Composable
private fun BoardLayout(sizeInNodes: Int, modifier: Modifier, content: @Composable () -> Unit) {
    Layout(content, modifier) { measurables, constraints ->
        val boardMaxSize = min(constraints.maxWidth, constraints.maxHeight)
        val nodeSize = (boardMaxSize / sizeInNodes).coerceAtMost(MAX_NODE_SIZE.roundToPx())
        val placeables = measurables.map { it.measure(Constraints.fixed(nodeSize, nodeSize)) }
        val boardSize = nodeSize * sizeInNodes

        layout(boardSize, boardSize) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(index % sizeInNodes * nodeSize, index / sizeInNodes * nodeSize)
            }
        }
    }
}

@Composable
private fun Node(state: BoardState, nodeIndex: Int, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(state.nodeIdToColor[nodeIndex])
            .border(1.dp, Color.Black)
    )
}

private fun Modifier.boardPointerInput(state: BoardState, boardWidthPx: Int) = pointerInput(boardWidthPx) {
    val nodeSizePx = boardWidthPx / state.boardSize

    coroutineScope {
        launch {
            detectDragGestures(
                onDragEnd = state::onPointerInputEnd,
                onDrag = { change, _ ->
                    val nodeId = getNodeIdFor(
                        change.position,
                        state,
                        boardWidthPx,
                        nodeSizePx
                    )
                    if (nodeId != null && state.onDrag(nodeId)) {
                        change.consume()
                    }
                }
            )
        }
        launch {
            detectTapGestures(
                onTap = { offset ->
                    getNodeIdFor(offset, state, boardWidthPx, nodeSizePx)
                        ?.let(state::onNodeClick)
                },
                onPress = { offset ->
                    getNodeIdFor(offset, state, boardWidthPx, nodeSizePx)
                        ?.let(state::onDragStart)
                }
            )
        }
    }
}

private fun getNodeIdFor(pointerPosition: Offset, state: BoardState, boardWidthPx: Int, nodeSizePx: Int): NodeId? {
    return if (
        pointerPosition.x > 0 &&
        pointerPosition.y > 0 &&
        pointerPosition.x < boardWidthPx &&
        pointerPosition.y < boardWidthPx
    ) {
        state.nodeIds[
            ((pointerPosition.y / nodeSizePx).toInt() * state.boardSize) + (pointerPosition.x / nodeSizePx).toInt()
        ]
    } else {
        null
    }
}

@Preview
@Composable
private fun BoardPreview() {
    Board(rememberBoardState(size = 20))
}