package pl.pathfinding.shared.domain.pathfinder

import pl.pathfinding.shared.domain.graph.StateGraph

interface PathfinderFactory {
    fun create(pathfinderType: PathfinderType, graph: StateGraph): Pathfinder
}

class DefaultPathfinderFactory : PathfinderFactory {
    override fun create(pathfinderType: PathfinderType, graph: StateGraph): Pathfinder {
        return when (pathfinderType) {
            PathfinderType.BREADTH_FIRST -> BreadthFirstPathfinder(graph)
            PathfinderType.DEPTH_FIRST -> DepthFirstPathfinder(graph)
        }
    }
}