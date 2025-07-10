package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import pl.pathfinding.shared.domain.Node
import pl.pathfinding.shared.domain.NodeState
import pl.pathfinding.shared.domain.Pathfinder
import pl.pathfinding.shared.domain.PathfinderFactory
import pl.pathfinding.shared.domain.PathfinderType

@Composable
internal fun rememberBoardState(size: Int): BoardState {
    return rememberSaveable(size, saver = BoardState.Saver) { BoardState(nodeCount = size, nodeStates = null) }
}

internal class BoardState private constructor(
    val nodeCount: Int,
    private val nodes: List<Node>
) {
    val nodeColors = nodes.map { derivedStateOf { it.state.color } }
    private var savedNodesStates: List<NodeState>? = null
    private var draggedNodeIndex: Int? = null
    private var nodeStateToToggleOnDrag: NodeState? = null
    private var previousDragNodeIndex: Int? = null
    var pathfinderType by mutableStateOf(PathfinderType.BREADTH_FIRST)
    private var searchState by mutableStateOf(SearchState.IDLE)
    val isBoardIdle by derivedStateOf { searchState == SearchState.IDLE }
    val isBoardSearchFinished by derivedStateOf { searchState == SearchState.FINISHED }
    var boardSizeInPixels = 0
        set(value) {
            field = value
            nodeSizeInPixels = value / nodeCount.toFloat()
        }
    private var nodeSizeInPixels: Float = 0f

    constructor(nodeStates: List<NodeState>?, nodeCount: Int) : this(nodeCount, NodeFactory().createNodes(nodeCount, nodeStates))

    fun onNodeClick(pointerPosition: Offset) {
        if (searchState == SearchState.IDLE) {
            getNodeIndexFor(pointerPosition)?.let(::toggleNodeIfEligible)
        }
        onPointerInputEnd()
    }

    fun onDragStart(pointerPosition: Offset) {
        if (searchState == SearchState.IDLE) {
            getNodeIndexFor(pointerPosition)?.let { nodeIndex ->
                val node = nodes[nodeIndex]
                if (node.state.isDraggable) {
                    draggedNodeIndex = nodeIndex
                    previousDragNodeIndex = nodeIndex
                } else {
                    nodeStateToToggleOnDrag = nodes[nodeIndex].state.toggleState
                }
            }
        }
    }

    fun onDrag(pointerPosition: Offset): Boolean {
        return if (searchState == SearchState.IDLE) {
            getNodeIndexFor(pointerPosition)?.let { nodeIndex ->
                onNodeDrag(nodeIndex)
                true
            } ?: false
        } else {
            false
        }
    }

    private fun onNodeDrag(nodeIndex: Int) {
        if (nodeIndex != previousDragNodeIndex) {
            val draggedNodeIndex = draggedNodeIndex
            if (draggedNodeIndex != null) {
                moveNode(draggedNodeIndex, nodeIndex)
            } else {
                toggleNodeIfEligible(nodeIndex)
                previousDragNodeIndex = nodeIndex
            }
        }
    }

    private fun moveNode(draggedNodeIndex: Int, destinationNodeIndex: Int) {
        if (nodes[destinationNodeIndex].state == NodeState.TRAVERSABLE) {
            nodes[destinationNodeIndex].state = nodes[draggedNodeIndex].state
            nodes[draggedNodeIndex].state = NodeState.TRAVERSABLE
            this.draggedNodeIndex = destinationNodeIndex
        }
    }

    private fun toggleNodeIfEligible(nodeIndex: Int) {
        val node = nodes[nodeIndex]
        val toggleState = node.state.toggleState
        if (toggleState != null) {
            nodes[nodeIndex].state = nodeStateToToggleOnDrag ?: toggleState
        }
    }

    private fun getNodeIndexFor(pointerPosition: Offset): Int? {
        check(nodeSizeInPixels > 0) { "Node size was larger than 0" }

        return if (
            pointerPosition.x > 0 &&
            pointerPosition.y > 0 &&
            pointerPosition.x < boardSizeInPixels &&
            pointerPosition.y < boardSizeInPixels
        ) {
            ((pointerPosition.y / nodeSizeInPixels).toInt() * nodeCount) + (pointerPosition.x / nodeSizeInPixels).toInt()
        } else {
            null
        }
    }

    fun onPointerInputEnd() {
        nodeStateToToggleOnDrag = null
        draggedNodeIndex = null
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun startSearch() {
        check(searchState == SearchState.IDLE) { "Search state is not idle: $searchState" }

        savedNodesStates = nodes.map(Node::state)
        searchState = SearchState.IN_PROGRESS
        val pathfinder = PathfinderFactory.create(pathfinderType, nodes)
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
        nodes.forEachIndexed { index, node ->
            if (node.state == NodeState.OBSTACLE) {
                nodes[index].state = NodeState.TRAVERSABLE
            }
        }
    }

    fun restoreBoard() {
        val savedNodesStates = checkNotNull(savedNodesStates)
        nodes.forEachIndexed { index, node ->
            node.state = savedNodesStates[index]
        }
        this.savedNodesStates = null
        searchState = SearchState.IDLE
    }

    companion object {
        private const val ANIMATION_DELAY_MILLIS = 5L

        val Saver = listSaver<BoardState, Any?>(
            save = { boardState ->
                listOf(
                    boardState.nodeCount,
                    boardState.savedNodesStates ?: boardState.nodes.map(Node::state),
                    boardState.pathfinderType
                )
            },
            restore = {
                @Suppress("UNCHECKED_CAST")
                BoardState(
                    nodeCount = it[0] as Int,
                    nodeStates = it[1] as List<NodeState>
                ).also { boardState ->
                    boardState.pathfinderType = it[2] as PathfinderType
                }
            }
        )

    }
}