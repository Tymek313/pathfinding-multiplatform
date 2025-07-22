package pl.pathfinding.shared.ui.component.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.pathfinding.shared.domain.node.NodeId
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

@Composable
internal fun Board(state: BoardState, modifier: Modifier = Modifier) {
    var boardSizeInNodes by remember { mutableIntStateOf(0) }
    var boardSizePx by remember { mutableIntStateOf(0) }
    var nodeSizePx by remember { mutableIntStateOf(0) }

    BoardLayout(
        onSizeInNodesChange = { boardSize, newNodeSize ->
            state.setupGraph(boardSize)
            boardSizeInNodes = boardSize
            nodeSizePx = newNodeSize
        },
        modifier = modifier
            .boardPointerInput(state, boardSizeInNodes, boardSizePx, nodeSizePx)
            .onSizeChanged { boardSizePx = it.width }
    ) { boardSize ->
        repeat(boardSize * boardSize) { nodeIndex ->
            Node(state, nodeIndex)
        }
    }
}

private val MINIMAL_NODE_SIZE = 30.dp

@Composable
private fun BoardLayout(
    onSizeInNodesChange: (boardSize: Int, nodeSize: Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (boardSize: Int) -> Unit
) {
    val density = LocalDensity.current
    val minimalNodeSizePx = density.run { MINIMAL_NODE_SIZE.roundToPx() }
    var boardSizeInNodes by remember { mutableIntStateOf(0) }

    SubcomposeLayout(modifier) { policy ->
        val boardSize = min(policy.maxWidth, policy.maxHeight)
        val newBoardSizeInNodes = boardSize / minimalNodeSizePx
        val nodeSize = (boardSize / newBoardSizeInNodes.toFloat())
        val nodeSizeRounded = nodeSize.roundToInt()

        if (newBoardSizeInNodes != boardSizeInNodes) {
            boardSizeInNodes = newBoardSizeInNodes
            onSizeInNodesChange(newBoardSizeInNodes, nodeSizeRounded)
        }

        val measurables = subcompose(Unit) { content(boardSizeInNodes) }

        var remainderForCurrentRow = (nodeSize * boardSizeInNodes - nodeSizeRounded * boardSizeInNodes).toInt()
        val nodeSizesInRowAndColumns = List(boardSize) { index ->
            val remainderUnitToApply = remainderForCurrentRow.sign
            remainderForCurrentRow -= remainderUnitToApply
            nodeSizeRounded + remainderUnitToApply
        }

        val placeables = List<Placeable>(boardSizeInNodes * boardSizeInNodes) { index ->
            val row = index / boardSizeInNodes
            val nodeInRowIndex = index % boardSizeInNodes
            measurables[row * boardSizeInNodes + nodeInRowIndex]
                .measure(Constraints.fixed(nodeSizesInRowAndColumns[nodeInRowIndex], nodeSizesInRowAndColumns[row]))
        }

        layout(boardSize, boardSize) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(
                    placeables.subList(0, index % boardSizeInNodes).sumOf { it.width },
                    placeables.getAbove(index, boardSizeInNodes).sumOf { it.height }
                )
            }
        }
    }
}

private fun List<Placeable>.getAbove(index: Int, rowSize: Int): List<Placeable> {
    return buildList {
        for (i in (index - rowSize) downTo 0 step rowSize) {
            add(this@getAbove[i])
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
    boardSizeInNodes: Int,
    boardSizePx: Int,
    nodeSizePx: Int
) = pointerInput(boardSizePx) {
    detectDragGestures(
        onDragEnd = state::onPointerInputEnd,
        onDrag = { change, _ ->
            val nodeId = change.position.toNodeIdOrNull(state.nodeIds, boardSizeInNodes, boardSizePx, nodeSizePx)
            if (nodeId != null && state.onDrag(nodeId)) {
                change.consume()
            }
        }
    )
}.pointerInput(boardSizePx) {
    detectTapGestures(
        onTap = { offset ->
            offset.toNodeIdOrNull(state.nodeIds, boardSizeInNodes, boardSizePx, nodeSizePx)?.let(state::onNodeClick)
        },
        onPress = { offset ->
            offset.toNodeIdOrNull(state.nodeIds, boardSizeInNodes, boardSizePx, nodeSizePx)?.let(state::onDragStart)
        }
    )
}

private fun Offset.toNodeIdOrNull(
    nodeIds: List<NodeId>,
    boardSizeInNodes: Int,
    boardWidthPx: Int,
    nodeSizePx: Int
): NodeId? {
    return if (x > 0 && y > 0 && x < boardWidthPx && y < boardWidthPx) {
        nodeIds[((y / nodeSizePx).toInt() * boardSizeInNodes) + (x / nodeSizePx).toInt()]
    } else {
        null
    }
}

@Preview
@Composable
private fun BoardPreview() {
    Board(rememberBoardState())
}