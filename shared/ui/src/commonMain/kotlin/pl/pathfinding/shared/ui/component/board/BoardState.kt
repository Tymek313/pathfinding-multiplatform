package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import pl.pathfinding.shared.domain.graph.Board
import pl.pathfinding.shared.domain.graph.DefaultStateGraph
import pl.pathfinding.shared.domain.graph.StateGraph
import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState
import pl.pathfinding.shared.domain.pathfinder.DefaultPathfinderFactory
import pl.pathfinding.shared.domain.pathfinder.Pathfinder
import pl.pathfinding.shared.domain.pathfinder.PathfinderFactory
import pl.pathfinding.shared.domain.pathfinder.PathfinderType

@Composable
internal fun rememberBoardState(size: Int): BoardState {
    return rememberSaveable(size, saver = BoardState.Saver) {
        BoardState(size, DefaultStateGraph(Board(size, size)))
    }
}

internal class BoardState(
    val boardSize: Int,
    private val graph: StateGraph,
    private val pathfinderFactory: PathfinderFactory = DefaultPathfinderFactory()
) {
    private val nodeStates by graph.nodeStatesAsState()
    val nodeIds get() = nodeStates.keys.toList()
    val nodeIdToColor get() = nodeStates.map { (_, nodeState) -> nodeState.color }
    private var graphSnapshot: StateGraph.Snapshot? = null
    private var draggedNodeId: NodeId? = null
    private var nodeStateToToggleOnDrag: NodeState? = null
    private var previousDragNodeIndex: NodeId? = null
    var pathfinderType by mutableStateOf(PathfinderType.BREADTH_FIRST)
    private var searchState by mutableStateOf(SearchState.IDLE)
    val isBoardIdle by derivedStateOf { searchState == SearchState.IDLE }
    val isBoardSearchFinished by derivedStateOf { searchState == SearchState.FINISHED }

    fun onNodeClick(id: NodeId) {
        if (searchState == SearchState.IDLE) {
            toggleNodeIfEligible(id)
        }
        onPointerInputEnd()
    }

    fun onDragStart(id: NodeId) {
        if (searchState == SearchState.IDLE) {
            val nodeState = graph[id]
            if (nodeState.isDraggable) {
                draggedNodeId = id
                previousDragNodeIndex = id
            } else {
                nodeStateToToggleOnDrag = nodeState.toggleState
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
        if (graph[destinationNode] == NodeState.TRAVERSABLE) {
            graph.swap(destinationNode, draggedNode)
            draggedNodeId = destinationNode
        }
    }

    private fun toggleNodeIfEligible(id: NodeId) {
        val nodeState = graph[id]
        val toggleState = nodeState.toggleState
        if (toggleState != null) {
            graph[id] = nodeStateToToggleOnDrag ?: toggleState
        }
    }

    fun onPointerInputEnd() {
        nodeStateToToggleOnDrag = null
        draggedNodeId = null
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun startSearch() {
        check(searchState == SearchState.IDLE) { "Search state is not idle: $searchState" }

        graphSnapshot = graph.createSnapshot()
        searchState = SearchState.IN_PROGRESS
        val pathfinder = pathfinderFactory.create(pathfinderType, graph)
        ticker(ANIMATION_DELAY_MILLIS).also { ticker ->
            ticker.consumeEach { advanceAnimation(ticker, pathfinder) }
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
        graph.removeObstacles()
    }

    fun restoreBoard() {
        graph.restoreFromSnapshot(checkNotNull(graphSnapshot))
        searchState = SearchState.IDLE
    }

    companion object {
        private const val ANIMATION_DELAY_MILLIS = 5L

        private val DefaultStateGraphSaver = listSaver<DefaultStateGraph.Snapshot, Any>(
            save = { it.toSerializedForm() },
            restore = { DefaultStateGraph.Snapshot.createFromSerialized(it) }
        )

        val Saver = listSaver<BoardState, Any>(
            save = { boardState ->
                listOf(
                    boardState.boardSize,
                    boardState.pathfinderType,
                    DefaultStateGraphSaver.run {
                        save(boardState.graph.createSnapshot() as DefaultStateGraph.Snapshot)!!
                    }
                )
            },
            restore = {
                val boardSize = it[0] as Int
                val pathfinderType = it[1] as PathfinderType
                val graphSnapshot = DefaultStateGraphSaver.run { restore(it[2])!! }
                @Suppress("UNCHECKED_CAST")
                BoardState(
                    boardSize = boardSize,
                    graph = DefaultStateGraph(Board(boardSize, boardSize)).apply {
                        restoreFromSnapshot(graphSnapshot)
                    }
                ).also { boardState ->
                    boardState.pathfinderType = pathfinderType
                }
            }
        )
    }
}

private fun StateGraph.nodeStatesAsState(): State<Map<NodeId, NodeState>> {
    val state = mutableStateOf(nodeStates, policy = neverEqualPolicy())
    onNodeStatesChange = { state.value = it }
    return state
}