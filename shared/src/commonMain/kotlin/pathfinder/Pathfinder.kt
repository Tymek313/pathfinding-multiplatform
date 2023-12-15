package pathfinder

interface Pathfinder {
    val searchFinished: Boolean
    fun stepForward(): Board
}



