package pathfinder

import pathfinder.Pathfinder.Progress
import ui.component.board.NodeState

class BreadthFirstPathfinder(nodes: List<NodeState>, private val rowSize: Int) : Pathfinder {
    private val nodes = nodes.toMutableList()
    private val nodeCount get() = nodes.size
    private val queuedNodes = ArrayDeque<VisitedNode>().apply(::initializeQueue)

    private fun initializeQueue(queue: ArrayDeque<VisitedNode>) {
        val startField = nodes.indexOf(NodeState.START)
        check(startField != -1)
        queue.addFirst(VisitedNode(index = startField, nodeState = QueuedNode.START, cameFrom = null))
    }

    override fun stepForward(): Progress {
        val searchEnded = searchNode(queuedNodes.removeFirst())
        return Progress(nodes.toMutableList(), searchEnded)
    }

    private fun searchNode(node: VisitedNode): Boolean {
        return when (node.nodeState) {
            QueuedNode.START -> handleStartNode(node)
            QueuedNode.QUEUED -> handleQueuedNode(node)
            QueuedNode.DESTINATION -> handleDestinationNode(node)
        }
    }

    private fun handleStartNode(node: VisitedNode): Boolean {
        addNeighborsToQueue(node)
        return false
    }

    private fun handleQueuedNode(node: VisitedNode): Boolean {
        val nodeIndex = node.index
        nodes[nodeIndex] = NodeState.VISITED
        addNeighborsToQueue(node)
        return queuedNodes.isEmpty()
    }

    private fun handleDestinationNode(node: VisitedNode): Boolean {
        markPath(node)
        return true
    }

    private fun addNeighborsToQueue(node: VisitedNode) {
        val nodeIndex = node.index
        val isFirstFieldInRow = nodeIndex % rowSize == 0
        val isLastFieldInRow = nodeIndex % rowSize == rowSize - 1
        val leftNeighbor = if (isFirstFieldInRow) null else nodeIndex - 1
        val rightNeighbor = if (isLastFieldInRow) null else nodeIndex + 1
        val topNeighbor = (nodeIndex - rowSize).takeIf { it >= 0 }
        val bottomNeighbor = (nodeIndex + rowSize).takeIf { it < nodeCount }

        listOfNotNull(leftNeighbor, rightNeighbor, topNeighbor, bottomNeighbor).forEach { neighborIndex ->
            addNeighborToQueue(node, neighborIndex)
        }
    }

    private fun addNeighborToQueue(node: VisitedNode, neighborIndex: Int) {
        val neighborField = nodes[neighborIndex]
        if (neighborField.isQueueable) {
            queuedNodes.addLast(VisitedNode(index = neighborIndex, nodeState = QueuedNode.from(neighborField), cameFrom = node))
            if (neighborField.isToggleable) {
                nodes[neighborIndex] = NodeState.QUEUED
            }
        }
    }

    private fun markPath(destinationNode: VisitedNode) {
        var cameFromNode = checkNotNull(destinationNode.cameFrom)
        while (cameFromNode.nodeState != QueuedNode.START) {
            nodes[cameFromNode.index] = NodeState.PATH
            cameFromNode = checkNotNull(cameFromNode.cameFrom)
        }
    }

    private class VisitedNode(val index: Int, val nodeState: QueuedNode, val cameFrom: VisitedNode?)

    private enum class QueuedNode {
        START, DESTINATION, QUEUED;

        companion object {
            fun from(nodeState: NodeState) = when (nodeState) {
                NodeState.START -> START
                NodeState.DESTINATION -> DESTINATION
                NodeState.EMPTY -> QUEUED
                NodeState.OBSTACLE,
                NodeState.PATH,
                NodeState.VISITED,
                NodeState.QUEUED -> error("No mapping found for $nodeState")
            }
        }
    }
}