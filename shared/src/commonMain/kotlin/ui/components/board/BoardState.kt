package ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import ui.pathfinder.PathfinderFactory
import ui.pathfinder.PathfinderType

@Composable
fun rememberBoardState(sizeX: Int, sizeY: Int): BoardState {
    val density = LocalDensity.current
    return rememberSaveable(sizeX, sizeY, density, saver = BoardState.Saver(density)) { BoardState(sizeX, sizeY, density) }
}

class BoardState private constructor(val nodeCountX: Int, val nodeCountY: Int, density: Density, savedNodes: List<NodeState>?) {

    constructor(sizeX: Int, sizeY: Int, density: Density) : this(sizeX, sizeY, density, null)

    val nodeDisplaySizeDp = 20.dp
    private val nodeDisplaySize = density.run { nodeDisplaySizeDp.roundToPx() }
    private val boardSize = IntSize(nodeCountX * nodeDisplaySize, nodeCountY * nodeDisplaySize)
    private var draggedNode: NodeState? = null
    private val isDraggingNode get() = draggedNode != null
    private var toggleToNodeState: NodeState? = null
    private var previousDragNodeIndex: NodeIndex = NodeIndex(-1)
    private val nodes = mutableStateListOf<NodeState>().apply { fillNodes(savedNodes) }
    private val nodesMap = mutableStateListOf<NodeState>()
    private var pathfinder by mutableStateOf(PathfinderType.BREADTH_FIRST)

    private fun SnapshotStateList<NodeState>.fillNodes(savedNodes: List<NodeState>?) {
        addAll(
            savedNodes ?: generateNodes(
                startPosition = getNodeIndex(x = 0, y = 0),
                endPosition = getNodeIndex(x = nodeCountX - 1, y = nodeCountY - 1)
            )
        )
    }

    private fun generateNodes(startPosition: Int, endPosition: Int): List<NodeState> {
        return (0..<nodeCountX * nodeCountY).map { index ->
            when (index) {
                startPosition -> NodeState.START
                endPosition -> NodeState.DESTINATION
                else -> NodeState.EMPTY
            }
        }
    }

    fun onNodeClick(pointerPosition: Offset) {
        getNodeIndexFor(pointerPosition)?.let { nodeIndex ->
            toggleNode(nodeIndex)
        }
    }

    fun onDragStart(pointerPosition: Offset) {
        getNodeIndexFor(pointerPosition)?.let { nodeIndex ->
            val node = nodes[nodeIndex]
            if (node.isDraggable) {
                draggedNode = node
                previousDragNodeIndex = nodeIndex
            } else {
                toggleToNodeState = nodes[nodeIndex].toggleState
            }
        }
    }

    fun onDrag(pointerPosition: Offset): Boolean {
        return getNodeIndexFor(pointerPosition)?.let { nodeIndex ->
            onNodeDrag(nodeIndex)
            true
        } ?: false
    }

    private fun onNodeDrag(nodeIndex: NodeIndex) {
        if (nodeIndex != previousDragNodeIndex) {
            if (isDraggingNode) {
                moveNode(nodeIndex)
            } else {
                toggleNode(nodeIndex)
                previousDragNodeIndex = nodeIndex
            }
        }
    }

    fun onDragEnd() {
        draggedNode = null
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun startSearch() {
        nodesMap.setAll(nodes)
        val pathfinder = PathfinderFactory.create(pathfinder, nodes, nodeCountX)
        val tickerChannel = ticker(delayMillis = 5)
        tickerChannel.consumeEach {
            val progress = pathfinder.stepForward()
            if (progress.searchFinished) {
                tickerChannel.cancel()
            }
            nodes.setAll(progress.nodes)
        }
    }

    private fun <T> MutableList<T>.setAll(items: Collection<T>) = apply {
        clear()
        addAll(items)
    }

    private fun getNodeIndexFor(pointerPosition: Offset): NodeIndex? {
        return if (pointerPosition.x < boardSize.width && pointerPosition.y < boardSize.height) {
            NodeIndex((pointerPosition.y.toInt() / nodeDisplaySize * nodeCountX) + (pointerPosition.x.toInt() / nodeDisplaySize))
        } else {
            null
        }
    }

    private fun moveNode(destinationNodeIndex: NodeIndex) {
        val sourceNodeIndex = nodes.indexOf(draggedNode)
        val destinationNode = nodes[destinationNodeIndex]

        if (destinationNode == NodeState.EMPTY) {
            nodes[destinationNodeIndex] = nodes[sourceNodeIndex]
            nodes[sourceNodeIndex] = NodeState.EMPTY
        }
    }

    private fun toggleNode(nodeIndex: NodeIndex) {
        val node = nodes[nodeIndex]
        if (node.isToggleable) {
            nodes[nodeIndex] = checkNotNull(toggleToNodeState)
        }
    }

    private fun getNodeIndex(y: Int, x: Int) = nodeCountX * y + x

    fun getNodeStateAtPosition(x: Int, y: Int): NodeState {
        return nodes[getNodeIndex(y, x)]
    }

    private operator fun SnapshotStateList<NodeState>.get(index: NodeIndex): NodeState {
        return this[index.index]
    }

    private operator fun SnapshotStateList<NodeState>.set(index: NodeIndex, newValue: NodeState) {
        this[index.index] = newValue
    }

    companion object {
        private const val KEY_SIZE_X = "sizeX"
        private const val KEY_SIZE_Y = "sizeY"
        private const val KEY_NODES = "nodes"

        @Suppress("UNCHECKED_CAST")
        fun Saver(density: Density) = mapSaver(
            save = { boardState ->
                mapOf(KEY_SIZE_X to boardState.nodeCountX, KEY_SIZE_Y to boardState.nodeCountY, KEY_NODES to boardState.nodes.toMutableList())
            },
            restore = { BoardState(it[KEY_SIZE_X] as Int, it[KEY_SIZE_Y] as Int, density, it[KEY_NODES] as List<NodeState>) }
        )
    }

    @JvmInline
    private value class NodeIndex(val index: Int)
}