package ui.component.board

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import pathfinder.Board
import pathfinder.Board.NodeIndex

class ObservableBoard private constructor(private val sizeX: Int, private val nodes: SnapshotStateList<NodeState>) : Board {

    private val nodeCount = nodes.size
    override val startNodeIndex = NodeIndex(nodes.indexOf(NodeState.START))

    constructor(sizeX: Int, sizeY: Int) : this(sizeX, generateNodes(sizeX, sizeY))

    override fun getNeighborsFor(index: NodeIndex): List<NodeIndex> {
        val nodeIndex = index.value
        val isFirstFieldInRow = nodeIndex % sizeX == 0
        val isLastFieldInRow = nodeIndex % sizeX == sizeX - 1

        val leftNeighbor = if (isFirstFieldInRow) null else NodeIndex(nodeIndex - 1)
        val rightNeighbor = if (isLastFieldInRow) null else NodeIndex(nodeIndex + 1)
        val topNeighbor = (nodeIndex - sizeX).takeIf { it >= 0 }?.let { NodeIndex(it) }
        val bottomNeighbor = (nodeIndex + sizeX).takeIf { it < nodeCount }?.let { NodeIndex(it) }

        return listOfNotNull(leftNeighbor, rightNeighbor, topNeighbor, bottomNeighbor)
    }

    override fun removeObstacles() {
        val iterator = nodes.listIterator()
        iterator.forEach {
            if (it == NodeState.OBSTACLE) {
                iterator.set(NodeState.EMPTY)
            }
        }
    }

    override fun copy(): ObservableBoard {
        return ObservableBoard(sizeX, nodes.toMutableStateList())
    }

    override fun clear() {
        nodes.clear()
    }

    override fun get(index: NodeIndex): NodeState {
        return nodes[index.value]
    }

    override fun set(index: NodeIndex, newValue: NodeState) {
        nodes[index.value] = newValue
    }

    override fun get(x: Int, y: Int): NodeState {
        return nodes[getNodeIndex(y, x)]
    }

    private fun getNodeIndex(y: Int, x: Int) = sizeX * y + x

    companion object {
        private fun generateNodes(sizeX: Int, sizeY: Int): SnapshotStateList<NodeState> {
            val startPosition = 0
            val destinationPosition = sizeX * sizeY - 1
            return mutableStateListOf<NodeState>().apply {
                (0..<sizeX * sizeY).forEach { index ->
                    add(
                        when (index) {
                            startPosition -> NodeState.START
                            destinationPosition -> NodeState.DESTINATION
                            else -> NodeState.EMPTY
                        }
                    )
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun Saver(): Saver<ObservableBoard, Any> {
            val keySizeX = "sizeX"
            val keyNodes = "nodes"

            return mapSaver(
                save = { mapOf(keySizeX to it.sizeX, keyNodes to it.nodes.toMutableList()) },
                restore = { ObservableBoard(sizeX = it[keySizeX] as Int, nodes = (it[keyNodes] as List<NodeState>).toMutableStateList()) }
            )
        }
    }
}