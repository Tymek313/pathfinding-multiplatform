package pl.pathfinding.shared.ui.component.board

import androidx.compose.ui.graphics.Color
import pl.pathfinding.shared.domain.node.NodeState

internal val NodeState.color: Color
    get() = when (this) {
        NodeState.START -> Color.Green
        NodeState.DESTINATION -> Color.Red
        NodeState.TRAVERSABLE -> Color.White
        NodeState.OBSTACLE -> Color.Black
        NodeState.PATH -> Color.Cyan
        NodeState.VISITED -> Color.Gray
        NodeState.QUEUED -> Color.Blue
    }

internal val NodeState.isDraggable: Boolean
    get() = when (this) {
        NodeState.START -> true
        NodeState.DESTINATION -> true
        NodeState.TRAVERSABLE -> false
        NodeState.OBSTACLE -> false
        NodeState.PATH -> false
        NodeState.VISITED -> false
        NodeState.QUEUED -> false
    }
