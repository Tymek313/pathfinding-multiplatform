package pl.pathfinding.shared.domain.pathfinder

import pl.pathfinding.shared.domain.graph.StateGraph
import pl.pathfinding.shared.domain.node.NodeState

internal abstract class AbstractPathfinder(private val graph: StateGraph) : Pathfinder {

    private val queuedNodes = ArrayDeque<Node>().apply {
        addFirst(
            Node(id = graph.startNodeId, cameFrom = null)
        )
    }

    override fun advance(): Boolean {
        check(queuedNodes.isNotEmpty()) { "No nodes to traverse" }

        val currentNode = queuedNodes.removeFirst()
        val traversingFinished = when (val state = graph[currentNode.id]) {
            NodeState.START -> {
                addEligibleNeighborsToQueue(currentNode)
                false
            }

            NodeState.QUEUED -> traverseQueuedNode(currentNode)
            NodeState.DESTINATION,
            NodeState.TRAVERSABLE,
            NodeState.OBSTACLE,
            NodeState.PATH,
            NodeState.VISITED -> error("Unexpected node state in the queue $state")
        }

        return traversingFinished
    }

    private fun traverseQueuedNode(node: Node): Boolean {
        graph[node.id] = NodeState.VISITED
        return addEligibleNeighborsToQueue(node)
    }

    private fun addEligibleNeighborsToQueue(currentNode: Node): Boolean {
        graph.getNeighbors(currentNode.id).forEach { neighbor ->
            val neighborState = graph[neighbor]
            if (neighborState == NodeState.DESTINATION) {
                markPath(destinationNode = Node(neighbor, currentNode))
                return true
            } else if (neighborState.isQueueable) {
                graph[neighbor] = NodeState.QUEUED
                queueNodeForTraversal(queuedNodes, Node(id = neighbor, cameFrom = currentNode))
            }
        }
        return queuedNodes.isEmpty()
    }

    abstract fun queueNodeForTraversal(deque: ArrayDeque<Node>, node: Node)

    private fun markPath(destinationNode: Node) {
        var cameFromNode = checkNotNull(destinationNode.cameFrom)

        while (graph[cameFromNode.id] != NodeState.START) {
            graph[cameFromNode.id] = NodeState.PATH
            cameFromNode = checkNotNull(cameFromNode.cameFrom)
        }
    }
}