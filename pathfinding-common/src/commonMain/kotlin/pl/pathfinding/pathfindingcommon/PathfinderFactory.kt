package pl.pathfinding.pathfindingcommon

object PathfinderFactory {

    fun create(pathfinderType: PathfinderType, board: Board): Pathfinder {
        return when (pathfinderType) {
            PathfinderType.BREADTH_FIRST -> BreadthFirstPathfinder(board)
        }
    }
}