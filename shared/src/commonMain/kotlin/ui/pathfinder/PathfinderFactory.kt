package ui.pathfinder

import ui.components.board.NodeState

object PathfinderFactory {

    fun create(pathfinderType: PathfinderType, initialBoardFields: List<NodeState>, rowSize: Int): Pathfinder {
        return when (pathfinderType) {
            PathfinderType.BREADTH_FIRST -> BreadthFirstPathfinder(initialBoardFields, rowSize)
        }
    }
}

enum class PathfinderType { BREADTH_FIRST }