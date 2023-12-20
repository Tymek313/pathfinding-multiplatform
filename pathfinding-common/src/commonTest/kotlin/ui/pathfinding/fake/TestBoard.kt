package ui.pathfinding.fake

import pathfinding.AbstractBoard
import pathfinding.Board
import pathfinding.NodeState

class TestBoard(private val rowSize: Int, private val nodes: MutableList<NodeState>) : AbstractBoard(rowSize, nodes) {

    constructor(initialNodes: List<List<NodeState>>) : this(initialNodes[0].size, initialNodes.flatten().toMutableList()) {
        require(initialNodes.all { it.size == initialNodes[0].size }) { "All rows must have the same size" }
    }

    override fun copy(): Board {
        return this
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TestBoard) {
            other.nodes == nodes
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = rowSize
        result = 31 * result + nodes.hashCode()
        result = 31 * result + startNodeIndex.hashCode()
        return result
    }
}

fun List<List<NodeState>>.asTestBoard() = TestBoard(this)
