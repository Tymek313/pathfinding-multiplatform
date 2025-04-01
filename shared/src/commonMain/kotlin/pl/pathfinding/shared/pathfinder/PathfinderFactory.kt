package pl.pathfinding.shared.pathfinder

internal object PathfinderFactory {

    fun create(pathfinderType: PathfinderType, nodes: List<Node>): Pathfinder {
        return when (pathfinderType) {
            PathfinderType.BREADTH_FIRST -> BreadthFirstPathfinder(nodes)
        }
    }
}