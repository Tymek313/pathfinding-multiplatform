package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset

@Composable
internal fun rememberBoardLayoutCoordinates() = remember { BoardLayoutCoordinates() }

internal class BoardLayoutCoordinates {

    var nodeIndexUpperOffsetsPx: List<Int>? = null
    var nodeSizesInRowAndColumns: List<Int>? = null
        set(value) {
            field = value
            var sum = 0
            nodeIndexUpperOffsetsPx = value!!.mapIndexed { index, nodeSize ->
                sum += nodeSize
                sum
            }
        }
    val boardSizeInNodes get() = nodeSizesInRowAndColumns!!.size
    val boardSizePx get() = nodeSizesInRowAndColumns!!.sum()

    fun getNodeIndex(pointerOffset: Offset): Int? {
        return if (
            pointerOffset.x > 0 &&
            pointerOffset.y > 0 &&
            pointerOffset.x < boardSizePx &&
            pointerOffset.y < boardSizePx
        ) {
            getNodeIndex(pointerOffset.y.toInt()) * boardSizeInNodes + getNodeIndex(pointerOffset.x.toInt())
        } else {
            null
        }
    }

    private fun getNodeIndex(nodeOffset: Int): Int {
        nodeIndexUpperOffsetsPx!!.forEachIndexed { index, offset ->
            if (nodeOffset <= offset) return index
        }
        error("Could not find breakpoint for pointer coordinates. Offset: $nodeOffset, breakpoints: $nodeIndexUpperOffsetsPx")
    }
}