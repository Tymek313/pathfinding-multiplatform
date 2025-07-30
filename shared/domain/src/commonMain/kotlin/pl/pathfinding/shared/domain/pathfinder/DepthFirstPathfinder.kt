package pl.pathfinding.shared.domain.pathfinder

import pl.pathfinding.shared.domain.graph.StateGraph

internal class DepthFirstPathfinder(graph: StateGraph) : AbstractPathfinder(graph) {
    override fun queueNodeForTraversal(deque: ArrayDeque<Node>, node: Node) {
        deque.addFirst(node)
    }
}
