package pl.pathfinding.shared.domain.graph

import pl.pathfinding.shared.domain.node.NodeId

interface Graph {
    val nodes: List<NodeId>
    fun getNeighbors(id: NodeId): List<NodeId>
    fun getCorrespondingId(idFromThisGraph: NodeId, otherGraph: Graph): NodeId?
}