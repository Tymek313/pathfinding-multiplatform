package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import pl.pathfinding.shared.domain.graph.DefaultStateGraph
import pl.pathfinding.shared.domain.graph.StateGraph
import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState
import pl.pathfinding.shared.domain.pathfinder.DefaultPathfinderFactory
import pl.pathfinding.shared.domain.pathfinder.Pathfinder
import pl.pathfinding.shared.domain.pathfinder.PathfinderFactory
import pl.pathfinding.shared.domain.pathfinder.PathfinderType

@Composable
internal fun rememberBoardState(): BoardState {
    return rememberSaveable(saver = BoardState.Saver) {
        BoardState()
    }
}

internal class BoardState(
    private var boardSize: Int = 0,
    private val graphFactory: StateGraphFactory = DefaultStateGraphFactory,
    private val pathfinderFactory: PathfinderFactory = DefaultPathfinderFactory(),
    initialGraph: StateGraph? = null
) {
    private var graph: StateGraph? = null
        set(graph) {
            field = graph
            if (graph != null) {
                graph.onNodeStatesChange = { _nodeStates = it }
                _nodeStates = graph.nodeStates
            }
        }
    private var _nodeStates by mutableStateOf(emptyMap<NodeId, NodeState>(), neverEqualPolicy())
    val nodeStates: Map<NodeId, NodeState> get() = _nodeStates
    val nodeIds get() = _nodeStates.keys.toList()
    val nodeIdToColor get() = _nodeStates.map { (_, nodeState) -> nodeState.color }
    private var graphSnapshot: StateGraph.Snapshot? = null
    private var draggedNodeId: NodeId? = null
    private var nodeStateToToggleOnDrag: NodeState? = null
    private var previousDragNodeIndex: NodeId? = null
    var pathfinderType by mutableStateOf(PathfinderType.BREADTH_FIRST)
    private var searchState by mutableStateOf(SearchState.IDLE)
    val isBoardIdle by derivedStateOf { searchState == SearchState.IDLE }
    val isBoardSearchFinished by derivedStateOf { searchState == SearchState.FINISHED }
    private var shouldCancelSearch = false

    init {
        graph = initialGraph // trigger setter
    }

    fun onBoardSizeChange(boardSizeInNodes: Int) {
        // Might be the case since board size doesn't mean that node count changed
        if (boardSizeInNodes == boardSize) {
            return
        }

        if (searchState == SearchState.IN_PROGRESS) {
            shouldCancelSearch = true
            graph!!.restoreFromSnapshot(graphSnapshot!!)
        }

        boardSize = boardSizeInNodes
        graph = graphFactory.create(boardSizeInNodes, graph)
    }

    fun onNodeClick(id: NodeId) {
        if (searchState == SearchState.IDLE) {
            toggleNodeIfEligible(id)
        }
        onPointerInputEnd()
    }

    fun onDragStart(id: NodeId) {
        if (searchState == SearchState.IDLE) {
            val nodeState = requireGraph()[id]
            if (nodeState.isDraggable) {
                draggedNodeId = id
                previousDragNodeIndex = id
            } else {
                nodeStateToToggleOnDrag = nodeState.toggleState
                toggleNodeIfEligible(id)
            }
        }
    }

    fun onDrag(id: NodeId): Boolean {
        return if (searchState == SearchState.IDLE) {
            onNodeDrag(id)
            true
        } else {
            false
        }
    }

    private fun onNodeDrag(id: NodeId) {
        if (id != previousDragNodeIndex) {
            val draggedNodeIndex = draggedNodeId
            if (draggedNodeIndex != null) {
                moveNode(draggedNodeIndex, id)
            } else {
                toggleNodeIfEligible(id)
                previousDragNodeIndex = id
            }
        }
    }

    private fun moveNode(draggedNode: NodeId, destinationNode: NodeId) {
        if (requireGraph()[destinationNode] == NodeState.TRAVERSABLE) {
            requireGraph().swapStates(destinationNode, draggedNode)
            draggedNodeId = destinationNode
        }
    }

    private fun toggleNodeIfEligible(id: NodeId) {
        val nodeState = requireGraph()[id]
        val toggleState = nodeState.toggleState
        if (toggleState != null) {
            requireGraph()[id] = nodeStateToToggleOnDrag ?: toggleState
        }
    }

    fun onPointerInputEnd() {
        nodeStateToToggleOnDrag = null
        draggedNodeId = null
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun startSearch() {
        check(searchState == SearchState.IDLE) { "Search state is not idle: $searchState" }

        graphSnapshot = requireGraph().createSnapshot()
        searchState = SearchState.IN_PROGRESS
        val pathfinder = pathfinderFactory.create(pathfinderType, requireGraph())
        ticker(ANIMATION_DELAY_MILLIS).also { ticker ->
            ticker.consumeEach {
                if (!pendingAnimationCancellationApplied(ticker)) {
                    advanceAnimation(ticker, pathfinder)
                }
            }
        }
    }

    private fun pendingAnimationCancellationApplied(ticker: ReceiveChannel<Unit>): Boolean {
        return if (shouldCancelSearch) {
            shouldCancelSearch = false
            searchState = SearchState.IDLE
            ticker.cancel()
            true
        } else {
            false
        }
    }

    private fun advanceAnimation(ticker: ReceiveChannel<Unit>, pathfinder: Pathfinder) {
        val searchFinished = pathfinder.advance()
        if (searchFinished) {
            searchState = SearchState.FINISHED
            ticker.cancel()
        }
    }

    fun removeObstacles() {
        requireGraph().removeAllObstacles()
    }

    fun restoreBoard() {
        requireGraph().restoreFromSnapshot(checkNotNull(graphSnapshot))
        graphSnapshot = null
        searchState = SearchState.IDLE
    }

    private fun requireGraph() = graph!!

    companion object {
        private const val ANIMATION_DELAY_MILLIS = 5L

        private val GraphSnapshotSaver = listSaver<StateGraph.Snapshot, Any>(
            save = { it.serialize() },
            restore = { DefaultStateGraph.Snapshot.createFromSerialized(it) }
        )

        val Saver = listSaver<BoardState, Any?>(
            save = { boardState ->
                listOf(
                    boardState.boardSize,
                    boardState.pathfinderType,
                    GraphSnapshotSaver.run {
                        // To avoid issues with ongoing animation state let's restore state from before animation
                        (boardState.graphSnapshot ?: boardState.graph?.createSnapshot())?.let { save(it) }
                    }
                )
            },
            restore = { values ->
                val boardSize = values[0] as Int
                val pathfinderType = values[1] as PathfinderType
                val graphSnapshot = values[2]?.let { GraphSnapshotSaver.run { restore(it) } }

                @Suppress("UNCHECKED_CAST")
                BoardState(
                    boardSize = boardSize,
                    initialGraph = DefaultStateGraphFactory.create(boardSize, null).apply {
                        if (graphSnapshot != null) {
                            restoreFromSnapshot(graphSnapshot)
                        }
                    }
                ).also { boardState ->
                    boardState.pathfinderType = pathfinderType
                }
            }
        )
    }
}