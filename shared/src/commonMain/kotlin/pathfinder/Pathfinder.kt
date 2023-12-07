package pathfinder

import ui.component.board.NodeState

interface Pathfinder {
    fun stepForward(): Progress

    data class Progress(val nodes: List<NodeState>, val searchFinished: Boolean)
}



