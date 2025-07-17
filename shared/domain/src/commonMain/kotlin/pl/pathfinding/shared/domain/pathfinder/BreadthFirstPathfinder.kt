package pl.pathfinding.shared.domain.pathfinder

import pl.pathfinding.shared.domain.graph.StateGraph
import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState

internal class BreadthFirstPathfinder(private val graph: StateGraph) : Pathfinder {

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
                addTraversableNeighborsToQueue(currentNode)
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
        return addTraversableNeighborsToQueue(node)
    }

    private fun addTraversableNeighborsToQueue(currentNode: Node): Boolean {
        graph.getNeighbors(currentNode.id).forEach { neighbor ->
            val neighborState = graph[neighbor]
            if(neighborState == NodeState.DESTINATION) {
                markPath(destinationNode = Node(neighbor, currentNode))
                return true
            } else if (neighborState.isQueueable) {
                graph[neighbor] = NodeState.QUEUED
                queuedNodes.addLast(Node(id = neighbor, cameFrom = currentNode))
            }
        }
        return queuedNodes.isEmpty()
    }

    private fun markPath(destinationNode: Node) {
        var cameFromNode = checkNotNull(destinationNode.cameFrom)

        while (graph[cameFromNode.id] != NodeState.START) {
            graph[cameFromNode.id] = NodeState.PATH
            cameFromNode = checkNotNull(cameFromNode.cameFrom)
        }
    }

    private class Node(val id: NodeId, val cameFrom: Node?)
}