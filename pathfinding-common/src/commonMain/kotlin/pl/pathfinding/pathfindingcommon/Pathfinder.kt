package pl.pathfinding.pathfindingcommon

interface Pathfinder {
    val searchFinished: Boolean
    fun stepForward(): Board
}



