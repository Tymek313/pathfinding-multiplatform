package pathfinder

import ui.component.board.NodeState

interface Board {
    val startNodeIndex: NodeIndex

    fun getNeighborsFor(index: NodeIndex): List<NodeIndex>
    fun removeObstacles()
    fun copy(): Board
    operator fun get(index: NodeIndex): NodeState
    operator fun get(x: Int, y: Int): NodeState
    operator fun set(index: NodeIndex, newValue: NodeState)

    @JvmInline
    value class NodeIndex(val value: Int)

    companion object
}