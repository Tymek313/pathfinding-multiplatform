package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

private val MINIMAL_NODE_SIZE = 30.dp

@Composable
internal fun BoardLayout(
    coordinates: BoardLayoutCoordinates,
    onSizeInNodesChange: (boardSizeInNodes: Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (nodeCount: Int) -> Unit
) {
    val density = LocalDensity.current
    val minimalNodeSizePx = density.run { MINIMAL_NODE_SIZE.roundToPx() }
    var boardSizeInNodes by remember { mutableIntStateOf(0) }

    SubcomposeLayout(modifier) { constraints ->
        val boardSize = min(constraints.maxWidth, constraints.maxHeight)
        val newBoardSizeInNodes = boardSize / minimalNodeSizePx

        if (newBoardSizeInNodes != boardSizeInNodes) {
            boardSizeInNodes = newBoardSizeInNodes
            onSizeInNodesChange(newBoardSizeInNodes)
        }

        val totalNodeCount = boardSizeInNodes * boardSizeInNodes
        val measurables = subcompose(Unit) { content(totalNodeCount) }

        val nodeSize = (boardSize / boardSizeInNodes.toFloat())
        val nodeSizeRounded = nodeSize.roundToInt()
        var remainderForCurrentRow = (nodeSize * boardSizeInNodes - nodeSizeRounded * boardSizeInNodes).toInt()
        val nodeSizesInRowsAndColumns = List(boardSizeInNodes) { index ->
            val remainderUnitToApply = remainderForCurrentRow.sign
            remainderForCurrentRow -= remainderUnitToApply
            nodeSizeRounded + remainderUnitToApply
        }

        coordinates.nodeSizesInRowAndColumns = nodeSizesInRowsAndColumns

        val placeables = List<Placeable>(totalNodeCount) { index ->
            val row = index / boardSizeInNodes
            val nodeInRowIndex = index % boardSizeInNodes
            measurables[row * boardSizeInNodes + nodeInRowIndex].measure(
                Constraints.fixed(nodeSizesInRowsAndColumns[nodeInRowIndex], nodeSizesInRowsAndColumns[row])
            )
        }

        layout(boardSize, boardSize) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(
                    getXNodePosition(index, nodeSizesInRowsAndColumns, boardSizeInNodes),
                    getYNodePosition(index, nodeSizesInRowsAndColumns, boardSizeInNodes)
                )
            }
        }
    }
}


private typealias GetIndexInAxisOperation = (Int, Int) -> Int

private val GetIndexInXAxisOperation: GetIndexInAxisOperation = Int::rem
private val GetIndexInYAxisOperation: GetIndexInAxisOperation = Int::div

private fun getXNodePosition(placeableIndex: Int, nodeSizes: List<Int>, boardSizeInNodes: Int) =
    getNodePosition(placeableIndex, nodeSizes, boardSizeInNodes, GetIndexInXAxisOperation)

private fun getYNodePosition(placeableIndex: Int, nodeSizes: List<Int>, boardSizeInNodes: Int) =
    getNodePosition(placeableIndex, nodeSizes, boardSizeInNodes, GetIndexInYAxisOperation)

private fun getNodePosition(
    placeableIndex: Int,
    nodeSizes: List<Int>,
    boardSizeInNodes: Int,
    getIndexInAxis: GetIndexInAxisOperation
): Int {
    var sum = 0
    for (i in 0..<getIndexInAxis(placeableIndex, boardSizeInNodes)) {
        sum += nodeSizes[i]
    }
    return sum
}