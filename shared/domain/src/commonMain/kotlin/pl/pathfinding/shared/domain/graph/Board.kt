package pl.pathfinding.shared.domain.graph

import pl.pathfinding.shared.domain.node.NodeId

class Board(private val size: Int) : Graph {

    init {
        require(size > 0) { "Size of the board should be greater than 0" }
    }

    override val nodes = List(size * size, ::NodeId)
    private val nodesAdjacency = nodes.associateWith { nodeId ->
        val nodeIndex = nodeId.value
        buildSet {
            val size = this@Board.size
            val top = if (nodeIndex < size) null else nodes[nodeIndex - size]
            val left = if (nodeIndex % size == 0) null else nodes[nodeIndex - 1]
            val right = if (nodeIndex % size == size - 1) null else nodes[nodeIndex + 1]
            val bottom = if (nodeIndex + size >= nodes.size) null else nodes[nodeIndex + size]

            if (top != null) add(top)
            if (left != null) add(left)
            if (right != null) add(right)
            if (bottom != null) add(bottom)
        }
    }

    override fun getNeighbors(id: NodeId) = nodesAdjacency.getValue(id)

    override fun getCorrespondingId(idFromThisGraph: NodeId, otherGraph: Graph): NodeId? {
        if (otherGraph !is Board) {
            return null
        }

        val nodeXFromThis = idFromThisGraph.value % size
        val nodeYFromThis = idFromThisGraph.value / size

        if (nodeXFromThis >= otherGraph.size || nodeYFromThis >= otherGraph.size) {
            return null
        }

        val boardSizeDifference = otherGraph.size - size

        return otherGraph.nodes[nodeYFromThis * size + nodeYFromThis * boardSizeDifference + nodeXFromThis]
    }
}
