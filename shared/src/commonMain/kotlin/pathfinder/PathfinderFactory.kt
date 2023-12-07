package pathfinder

import ui.component.board.NodeState

object PathfinderFactory {

    fun create(pathfinderType: PathfinderType, initialBoardFields: List<NodeState>, rowSize: Int): Pathfinder {
        return when (pathfinderType) {
            PathfinderType.BREADTH_FIRST -> BreadthFirstPathfinder(initialBoardFields, rowSize)
        }
    }
}