package pl.pathfinding.shared.domain.graph

import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState

class DefaultStateGraph(private val graph: Graph) : StateGraph, Graph by graph {

    private val _nodeStates = run {
        var index = 0
        graph.nodes.associateWith { nodeId ->
            when (index++) {
                0 -> NodeState.START
                graph.nodes.size - 1 -> NodeState.DESTINATION
                else -> NodeState.TRAVERSABLE
            }
        }.toMutableMap()
    }
    override val nodeStates: Map<NodeId, NodeState> = _nodeStates
    override val startNodeId get() = _nodeStates.firstNotNullOf { (id, state) ->
        if(state == NodeState.START) id else null
    }
    override var onNodeStatesChange: ((Map<NodeId, NodeState>) -> Unit)? = null

    override operator fun get(id: NodeId): NodeState = nodeStates.getValue(id)

    override fun set(id: NodeId, state: NodeState) {
        _nodeStates[id] = state
        onNodeStatesChange?.invoke(_nodeStates)
    }

    override fun swapStates(id1: NodeId, id2: NodeId) {
        val state1 = get(id1)
        _nodeStates[id1] = get(id2)
        _nodeStates[id2] = state1
        onNodeStatesChange?.invoke(_nodeStates)
    }

    override fun removeAllObstacles() {
        _nodeStates.forEach { (id, state) ->
            if (state == NodeState.OBSTACLE) {
                _nodeStates[id] = NodeState.TRAVERSABLE
            }
        }
        onNodeStatesChange?.invoke(_nodeStates)
    }

    override fun createSnapshot(): StateGraph.Snapshot = DefaultSnapshot(_nodeStates.toMutableMap())

    override fun restoreFromSnapshot(snapshot: StateGraph.Snapshot) {
        (snapshot as DefaultSnapshot).restore(this)
    }

    interface Snapshot : StateGraph.Snapshot {
        companion object {
            @Suppress("UNCHECKED_CAST")
            fun createFromSerialized(values: List<Any>): Snapshot =
                DefaultSnapshot((values[0] as Map<Int, NodeState>).mapKeys { (idValue, _) -> NodeId(idValue) })
        }
    }

    private class DefaultSnapshot(val nodeStates: Map<NodeId, NodeState>) : Snapshot {
        fun restore(stateGraph: DefaultStateGraph) {
            stateGraph._nodeStates.putAll(nodeStates)
            stateGraph.onNodeStatesChange?.invoke(nodeStates)
        }

        override fun toSerializedForm(): List<Any> = listOf(nodeStates.mapKeys { (id, _) -> id.value })
    }
}