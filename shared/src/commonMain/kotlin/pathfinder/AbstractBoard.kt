package pathfinder

import pathfinder.Board.NodeIndex
import ui.component.board.NodeState

abstract class AbstractBoard(private val rowSize: Int, private val nodes: MutableList<NodeState>) : Board {

    private val nodeCount = nodes.size
    override val startNodeIndex = NodeIndex(nodes.indexOf(NodeState.START))

    override fun getNeighborsFor(index: NodeIndex): Set<NodeIndex> {
        val nodeIndex = index.value
        val isFirstFieldInRow = nodeIndex % rowSize == 0
        val isLastFieldInRow = nodeIndex % rowSize == rowSize - 1

        val leftNeighbor = if (isFirstFieldInRow) null else NodeIndex(nodeIndex - 1)
        val rightNeighbor = if (isLastFieldInRow) null else NodeIndex(nodeIndex + 1)
        val topNeighbor = (nodeIndex - rowSize).takeIf { it >= 0 }?.let { NodeIndex(it) }
        val bottomNeighbor = (nodeIndex + rowSize).takeIf { it < nodeCount }?.let { NodeIndex(it) }

        return setOfNotNull(leftNeighbor, rightNeighbor, topNeighbor, bottomNeighbor)
    }

    override fun removeObstacles() {
        val iterator = nodes.listIterator()
        iterator.forEach {
            if (it == NodeState.OBSTACLE) {
                iterator.set(checkNotNull(it.toggleState))
            }
        }
    }

    override fun get(index: NodeIndex): NodeState {
        return nodes[index.value]
    }

    override fun set(index: NodeIndex, newValue: NodeState) {
        nodes[index.value] = newValue
    }

    override fun get(x: Int, y: Int): NodeState {
        return nodes[rowSize * y + x]
    }
}