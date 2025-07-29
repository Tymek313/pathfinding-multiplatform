package pl.pathfinding.shared.ui.testutils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.TouchInjectionScope

class BoardTouchInjectionScope(
    private val nodeSize: Float,
    delegate: TouchInjectionScope
) : TouchInjectionScope by delegate {

    fun TouchInjectionScope.downOnNodeFromBottomRight(bottomOffsetInNodes: Int, rightOffsetInNodes: Int = 0) {
        require(bottomOffsetInNodes >= 0)
        require(rightOffsetInNodes >= 0)

        down(
            bottomRight - Offset(
                nodeSize * rightOffsetInNodes + nodeSize / 2,
                nodeSize * bottomOffsetInNodes + nodeSize / 2
            )
        )
    }

    fun TouchInjectionScope.downOnNodeFromTopLeft(leftOffsetInNodes: Int) {
        require(leftOffsetInNodes >= 0)
        down(Offset(nodeSize * leftOffsetInNodes + nodeSize / 2, nodeSize / 2))
    }

    fun TouchInjectionScope.moveByNodes(xInNodes: Int = 0, yInNodes: Int = 0) {
        moveBy(Offset(nodeSize * xInNodes, nodeSize * yInNodes))
    }
}