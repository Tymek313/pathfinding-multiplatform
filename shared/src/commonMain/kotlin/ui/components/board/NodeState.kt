package ui.components.board

import androidx.compose.ui.graphics.Color

enum class NodeState(val color: Color, val isDraggable: Boolean, val isQueueable: Boolean) {
    START(Color.Green, isDraggable = true, isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    DESTINATION(Color.Red, isDraggable = true, isQueueable = true) {
        override val toggleState: NodeState? = null
    },
    EMPTY(Color.White, isDraggable = false, isQueueable = true) {
        override val toggleState get() = OBSTACLE
    },
    OBSTACLE(Color.Black, isDraggable = false, isQueueable = false) {
        override val toggleState get() = EMPTY
    },
    PATH(Color.Cyan, isDraggable = false, isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    VISITED(Color.Gray, isDraggable = false, isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    QUEUED(Color.Blue, isDraggable = false, isQueueable = false) {
        override val toggleState: NodeState? = null
    };

    abstract val toggleState: NodeState?
    val isToggleable get() = toggleState != null
}