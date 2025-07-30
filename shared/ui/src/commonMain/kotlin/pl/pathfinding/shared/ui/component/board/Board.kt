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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pathfinding.shared.ui.generated.resources.Res
import pathfinding.shared.ui.generated.resources.node_state_destination
import pathfinding.shared.ui.generated.resources.node_state_obstacle
import pathfinding.shared.ui.generated.resources.node_state_path
import pathfinding.shared.ui.generated.resources.node_state_queued
import pathfinding.shared.ui.generated.resources.node_state_start
import pathfinding.shared.ui.generated.resources.node_state_traversable
import pathfinding.shared.ui.generated.resources.node_state_visited
import pl.pathfinding.shared.domain.node.NodeState

internal const val TEST_TAG_BOARD = "board"

@Composable
internal fun Board(state: BoardState, modifier: Modifier = Modifier) {
    val coordinates = rememberBoardLayoutCoordinates()

    BoardLayout(
        coordinates,
        modifier = modifier.testTag(TEST_TAG_BOARD).boardPointerInput(state, coordinates)
    ) { nodeCount, boardSizeInNodes ->

        state.onBoardSizeChange(boardSizeInNodes)

        repeat(nodeCount) { nodeIndex ->
            Node(state, nodeIndex)
        }
    }
}

@Composable
private fun Node(state: BoardState, nodeIndex: Int, modifier: Modifier = Modifier) {
    val nodeState by remember {
        derivedStateOf { state.nodeStates.getValue(state.nodeIds[nodeIndex]) }
    }
    val color by remember { derivedStateOf { state.nodeIdToColor[nodeIndex] } }
    val nodeStateDescription = stringResource(nodeState.toStateDescriptionRes())

    Box(
        modifier
            .background(color)
            .border(1.dp, Color.Black)
            .semantics { stateDescription = nodeStateDescription }
    )
}

private fun NodeState.toStateDescriptionRes() = when (this) {
    NodeState.START -> Res.string.node_state_start
    NodeState.DESTINATION -> Res.string.node_state_destination
    NodeState.TRAVERSABLE -> Res.string.node_state_traversable
    NodeState.OBSTACLE -> Res.string.node_state_obstacle
    NodeState.PATH -> Res.string.node_state_path
    NodeState.VISITED -> Res.string.node_state_visited
    NodeState.QUEUED -> Res.string.node_state_queued
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