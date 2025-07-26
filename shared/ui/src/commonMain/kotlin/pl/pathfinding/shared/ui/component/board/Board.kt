package pl.pathfinding.shared.ui.component.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun Board(state: BoardState, modifier: Modifier = Modifier) {
    val coordinates = rememberBoardLayoutCoordinates()

    BoardLayout(
        coordinates,
        modifier = modifier.boardPointerInput(state, coordinates)
    ) { nodeCount, boardSizeInNodes ->

        state.onBoardSizeChange(boardSizeInNodes)

        repeat(nodeCount) { nodeIndex ->
            Node(state, nodeIndex)
        }
    }
}

@Composable
private fun Node(state: BoardState, nodeIndex: Int, modifier: Modifier = Modifier) {
    val color by remember { derivedStateOf { state.nodeIdToColor[nodeIndex] } }
    Box(
        modifier
            .background(color)
            .border(1.dp, Color.Black)
    )
}

private fun Modifier.boardPointerInput(
    state: BoardState,
    boardCoordinates: BoardLayoutCoordinates,
) = pointerInput(Unit) {
    detectDragGestures(
        onDragEnd = state::onPointerInputEnd,
        onDrag = { change, _ ->
            val nodeId = boardCoordinates.getNodeIndex(change.position)?.let(state.nodeIds::get)
            if (nodeId != null && state.onDrag(nodeId)) {
                change.consume()
            }
        }
    )
}.pointerInput(Unit) {
    detectTapGestures(
        onTap = { offset ->
            boardCoordinates.getNodeIndex(offset)?.let(state.nodeIds::get)?.let(state::onNodeClick)
        },
        onPress = { offset ->
            boardCoordinates.getNodeIndex(offset)?.let(state.nodeIds::get)?.let(state::onDragStart)
        }
    )
}

@Preview
@Composable
private fun BoardPreview() {
    Board(rememberBoardState())
}