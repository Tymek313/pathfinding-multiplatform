package pl.pathfinding.shared.domain.graph

import pl.pathfinding.shared.domain.node.NodeId

class Board(private val sizeX: Int, private val sizeY: Int) : Graph {

    override val nodes = List(sizeX * sizeY, ::NodeId)
    private val nodesAdjacency = nodes.associateWith { nodeId ->
        val nodeIndex = nodeId.value
        buildList {
            val top = if (nodeIndex < sizeY) null else nodes[nodeIndex - sizeX]
            val left = if (nodeIndex % sizeX == 0) null else nodes[nodeIndex - 1]
            val right = if (nodeIndex % sizeX == sizeX - 1) null else nodes[nodeIndex + 1]
            val bottom = if (nodeIndex + sizeX >= nodes.size) null else nodes[nodeIndex + sizeX]

            if (top != null) add(top)
            if (left != null) add(left)
            if (right != null) add(right)
            if (bottom != null) add(bottom)
        }
    }

    override fun getNeighbors(id: NodeId): List<NodeId> = nodesAdjacency.getValue(id)

    override fun getCorrespondingId(idFromThisGraph: NodeId, otherGraph: Graph): NodeId? {
        if (otherGraph !is Board) {
            return null
        }

        val nodeXFromThis = idFromThisGraph.value % sizeY
        val nodeYFromThis = idFromThisGraph.value / sizeY

        if (nodeXFromThis >= otherGraph.sizeX || nodeYFromThis >= otherGraph.sizeY) {
            return null
        }

        val sizeYDifference = otherGraph.sizeY - sizeY

        return otherGraph.nodes[nodeYFromThis * sizeX + nodeYFromThis * sizeYDifference + nodeXFromThis]
    }
}
