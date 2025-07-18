package pl.pathfinding.shared.domain.pathfinder

import pl.pathfinding.shared.domain.graph.StateGraph

internal class BreadthFirstPathfinder(graph: StateGraph) : CommonPathfinder(graph) {
    override fun queueNodeForTraversal(deque: ArrayDeque<Node>, node: Node) {
        deque.addLast(node)
    }
}