package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset

@Composable
internal fun rememberBoardLayoutCoordinates() = remember { BoardLayoutCoordinates() }

internal class BoardLayoutCoordinates {

    private var nodeIndexUpperOffsetsPx: List<Int>? = null
    private var boardSizeInNodes = 0
    private var boardSizePx = 0

    fun setNodeSizesInRowAndColumns(sizes: List<Int>) {
        var sum = 0
        nodeIndexUpperOffsetsPx = sizes.mapIndexed { index, nodeSize ->
            sum += nodeSize
            sum
        }
        boardSizeInNodes = sizes.size
        boardSizePx = sizes.sum()
    }

    fun getNodeIndex(pointerOffset: Offset): Int? = if (
        pointerOffset.x > 0 &&
        pointerOffset.y > 0 &&
        pointerOffset.x < boardSizePx &&
        pointerOffset.y < boardSizePx
    ) {
        getNodeIndex(pointerOffset.y.toInt()) * boardSizeInNodes + getNodeIndex(pointerOffset.x.toInt())
    } else {
        null
    }

    private fun getNodeIndex(nodeOffset: Int): Int {
        nodeIndexUpperOffsetsPx!!.forEachIndexed { index, offset ->
            if (nodeOffset <= offset) return index
        }
        error(
            "Could not find breakpoint for pointer coordinates. Offset: $nodeOffset, breakpoints: $nodeIndexUpperOffsetsPx"
        )
    }
}
