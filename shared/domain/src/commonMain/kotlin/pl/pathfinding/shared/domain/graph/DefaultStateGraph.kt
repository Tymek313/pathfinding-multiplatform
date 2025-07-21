package pl.pathfinding.shared.domain.graph

import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState

class DefaultStateGraph(
    override val originalGraph: Graph,
    previousGraph: StateGraph? = null
) : StateGraph, Graph by originalGraph {

    private val _nodeStates = if (previousGraph == null) {
        createNodeStatesFromScratch()
    } else {
        createNodeStatesUsingPreviousGraph(previousGraph)
    }
    override val nodeStates: Map<NodeId, NodeState> = _nodeStates
    override val startNodeId
        get() = _nodeStates.firstNotNullOf { (id, state) ->
            if (state == NodeState.START) id else null
        }
    override var onNodeStatesChange: ((Map<NodeId, NodeState>) -> Unit)? = null

    private fun createNodeStatesFromScratch() = nodes.associateWith { nodeId ->
        when (nodeId.value) {
            0 -> NodeState.START
            nodes.size - 1 -> NodeState.DESTINATION
            else -> NodeState.TRAVERSABLE
        }
    }.toMutableMap()

    private fun createNodeStatesUsingPreviousGraph(previousGraph: StateGraph): MutableMap<NodeId, NodeState> {
        val states = nodes.associateWith { nodeId ->
            getCorrespondingId(nodeId, previousGraph.originalGraph)
                ?.let(previousGraph::get)
                ?: NodeState.TRAVERSABLE
        }.toMutableMap()

        var shouldAddStartNode = true
        var shouldAddDestinationNode = true

        for ((_, nodeState) in states) {
            if (nodeState == NodeState.START) {
                shouldAddStartNode = false
                if (!shouldAddDestinationNode) break
            } else if (nodeState == NodeState.DESTINATION) {
                shouldAddDestinationNode = false
                if (!shouldAddStartNode) break
            }
        }

        if (shouldAddStartNode) {
            states[nodes.first()] = NodeState.START
        }

        if (shouldAddDestinationNode) {
            states[nodes.last()] = NodeState.DESTINATION
        }

        return states
    }

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

        override fun serialize(): List<Any> = listOf(nodeStates.mapKeys { (id, _) -> id.value })
    }
}