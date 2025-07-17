package pl.pathfinding.shared.domain.graph

import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState

interface StateGraph : Graph {
    val nodeStates: Map<NodeId, NodeState>
    val startNodeId: NodeId
    var onNodeStatesChange: ((Map<NodeId, NodeState>) -> Unit)?
    operator fun get(id: NodeId): NodeState
    operator fun set(id: NodeId, state: NodeState)
    fun swap(id1: NodeId, id2: NodeId)
    fun removeObstacles()
    fun createSnapshot(): Snapshot
    fun restoreFromSnapshot(snapshot: Snapshot)

    interface Snapshot {
        fun toSerializedForm(): List<Any>
    }
}