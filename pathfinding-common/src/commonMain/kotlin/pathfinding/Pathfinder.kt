package pathfinding

interface Pathfinder {
    val searchFinished: Boolean
    fun stepForward(): Board
}



