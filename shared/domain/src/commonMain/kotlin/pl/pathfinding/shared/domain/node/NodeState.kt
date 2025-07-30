package pl.pathfinding.shared.domain.node

enum class NodeState(val isQueueable: Boolean) {
    START(isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    DESTINATION(isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    TRAVERSABLE(isQueueable = true) {
        override val toggleState get() = OBSTACLE
    },
    OBSTACLE(isQueueable = false) {
        override val toggleState get() = TRAVERSABLE
    },
    PATH(isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    VISITED(isQueueable = false) {
        override val toggleState: NodeState? = null
    },
    QUEUED(isQueueable = false) {
        override val toggleState: NodeState? = null
    };

    abstract val toggleState: NodeState?
}