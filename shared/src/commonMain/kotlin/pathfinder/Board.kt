package pathfinder

interface Board {
    val startNodeIndex: NodeIndex

    fun getNeighborsFor(index: NodeIndex): Set<NodeIndex>
    fun removeObstacles()
    fun copy(): Board
    operator fun get(index: NodeIndex): NodeState
    operator fun get(x: Int, y: Int): NodeState
    operator fun set(index: NodeIndex, newValue: NodeState)

    @JvmInline
    value class NodeIndex(val value: Int) {
        init {
            require(value >= 0) { "Node index cannot be negative" }
        }
    }

    companion object
}