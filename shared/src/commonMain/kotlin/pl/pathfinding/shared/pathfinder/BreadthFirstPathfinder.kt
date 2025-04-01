package pl.pathfinding.shared.pathfinder

internal class BreadthFirstPathfinder(nodes: List<Node>) : Pathfinder {

    private val queuedNodes = ArrayDeque<VisitedNode>().apply {
        addFirst(
            VisitedNode(
                node = nodes.first { it.state == NodeState.START },
                cameFrom = null
            )
        )
    }

    override fun advance(): Boolean {
        check(queuedNodes.isNotEmpty()) { "No nodes to traverse" }

        val currentNode = queuedNodes.removeFirst()
        return when (currentNode.node.state) {
            NodeState.START -> traverseStartNode(currentNode)
            NodeState.QUEUED -> traverseQueuedNode(currentNode)
            NodeState.DESTINATION -> markPath(currentNode)
            NodeState.TRAVERSABLE,
            NodeState.OBSTACLE,
            NodeState.PATH,
            NodeState.VISITED -> error("Unexpected node state in the queue ${currentNode.node.state}")
        }
    }

    private fun traverseStartNode(node: VisitedNode): Boolean {
        addTraversableNeighborsToQueue(node)
        return false
    }

    private fun traverseQueuedNode(node: VisitedNode): Boolean {
        node.node.state = NodeState.VISITED
        addTraversableNeighborsToQueue(node)
        return queuedNodes.isEmpty()
    }

    private fun addTraversableNeighborsToQueue(currentNode: VisitedNode) {
        currentNode.node.neighbors.forEach { neighbor ->
            if (neighbor.state.isQueueable) {
                queuedNodes.addLast(VisitedNode(node = neighbor, cameFrom = currentNode))
                if (neighbor.state == NodeState.TRAVERSABLE) {
                    neighbor.state = NodeState.QUEUED
                }
            }
        }
    }

    private fun markPath(destinationNode: VisitedNode): Boolean {
        var cameFromNode = checkNotNull(destinationNode.cameFrom)
        while (cameFromNode.node.state != NodeState.START) {
            cameFromNode.node.state = NodeState.PATH
            cameFromNode = checkNotNull(cameFromNode.cameFrom)
        }
        return true
    }

    private class VisitedNode(val node: Node, val cameFrom: VisitedNode?)
}