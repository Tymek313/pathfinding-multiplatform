package pl.pathfinding.pathfindingcommon

enum class NodeState(val isDraggable: Boolean, val isQueueable: Boolean) {
    START(isDraggable = true, isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    DESTINATION(isDraggable = true, isQueueable = true) {
        override val toggleState: NodeState? = null
    },
    EMPTY(isDraggable = false, isQueueable = true) {
        override val toggleState get() = OBSTACLE
    },
    OBSTACLE(isDraggable = false, isQueueable = false) {
        override val toggleState get() = EMPTY
    },
    PATH(isDraggable = false, isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    VISITED(isDraggable = false, isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    QUEUED(isDraggable = false, isQueueable = false) {
        override val toggleState: NodeState? = null
    };

    abstract val toggleState: NodeState?
    val isToggleable get() = toggleState != null
}