package pl.pathfinding.shared.domain.pathfinder

import pl.pathfinding.shared.domain.graph.StateGraph
import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState

internal class DepthFirstPathfinder(graph: StateGraph) : CommonPathfinder(graph) {
    override fun queueNodeForTraversal(deque: ArrayDeque<Node>, node: Node) {
        deque.addFirst(node)
    }
}