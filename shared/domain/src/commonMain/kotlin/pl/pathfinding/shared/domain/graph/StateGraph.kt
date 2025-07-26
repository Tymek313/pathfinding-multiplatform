package pl.pathfinding.shared.domain.graph

import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState

interface StateGraph : Graph {
    val originalGraph: Graph
    val nodeStates: Map<NodeId, NodeState>
    var onNodeStatesChange: ((Map<NodeId, NodeState>) -> Unit)?
    operator fun get(id: NodeId): NodeState
    operator fun set(id: NodeId, state: NodeState)
    fun swapStates(id1: NodeId, id2: NodeId)
    fun removeAllObstacles()
    fun createSnapshot(): Snapshot
    fun restoreFromSnapshot(snapshot: Snapshot)

    interface Snapshot {
        fun serialize(): List<Any>
    }
}

val StateGraph.startNodeId
    get() = nodeStates.firstNotNullOf { (id, state) ->
        if (state == NodeState.START) id else null
    }