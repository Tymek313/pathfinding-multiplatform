package pl.pathfinding.pathfindingcommon

import pl.pathfinding.pathfindingcommon.Board.NodeIndex

internal class BreadthFirstPathfinder(board: Board) : Pathfinder {

    private val board = board.copy()
    private val queuedNodes = ArrayDeque<VisitedNode>().apply(::initializeQueue)
    override var searchFinished = false

    private fun initializeQueue(queue: ArrayDeque<VisitedNode>) {
        queue.addFirst(VisitedNode(index = board.startNodeIndex, nodeState = QueuedNode.START, cameFrom = null))
    }

    override fun stepForward(): Board {
        searchFinished = searchNode(queuedNodes.removeFirst())
        return board.copy()
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
        board[node.index] = NodeState.VISITED
        addNeighborsToQueue(node)
        return queuedNodes.isEmpty()
    }

    private fun handleDestinationNode(node: VisitedNode): Boolean {
        markPath(node)
        return true
    }

    private fun addNeighborsToQueue(node: VisitedNode) {
        board.getNeighborsFor(node.index).forEach { neighborIndex ->
            addNeighborToQueue(node, neighborIndex)
        }
    }

    private fun addNeighborToQueue(node: VisitedNode, neighborIndex: NodeIndex) {
        val neighborField = board[neighborIndex]
        if (neighborField.isQueueable) {
            queuedNodes.addLast(VisitedNode(index = neighborIndex, nodeState = QueuedNode.from(neighborField), cameFrom = node))
            if (neighborField.isToggleable) {
                board[neighborIndex] = NodeState.QUEUED
            }
        }
    }

    private fun markPath(destinationNode: VisitedNode) {
        var cameFromNode = checkNotNull(destinationNode.cameFrom)
        while (cameFromNode.nodeState != QueuedNode.START) {
            board[cameFromNode.index] = NodeState.PATH
            cameFromNode = checkNotNull(cameFromNode.cameFrom)
        }
    }

    private class VisitedNode(val index: NodeIndex, val nodeState: QueuedNode, val cameFrom: VisitedNode?)

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