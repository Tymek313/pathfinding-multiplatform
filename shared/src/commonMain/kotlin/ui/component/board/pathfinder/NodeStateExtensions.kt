package ui.component.board.pathfinder

import androidx.compose.ui.graphics.Color
import pathfinder.NodeState

val NodeState.color: Color
    get() = when (this) {
        NodeState.START -> Color.Green
        NodeState.DESTINATION -> Color.Red
        NodeState.EMPTY -> Color.White
        NodeState.OBSTACLE -> Color.Black
        NodeState.PATH -> Color.Cyan
        NodeState.VISITED -> Color.Gray
        NodeState.QUEUED -> Color.Blue
    }