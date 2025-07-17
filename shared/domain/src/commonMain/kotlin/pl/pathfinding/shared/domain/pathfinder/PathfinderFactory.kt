package pl.pathfinding.shared.domain.pathfinder

import pl.pathfinding.shared.domain.graph.StateGraph

object PathfinderFactory {

    fun create(pathfinderType: PathfinderType, graph: StateGraph): Pathfinder {
        return when (pathfinderType) {
            PathfinderType.BREADTH_FIRST -> BreadthFirstPathfinder(graph)
        }
    }
}