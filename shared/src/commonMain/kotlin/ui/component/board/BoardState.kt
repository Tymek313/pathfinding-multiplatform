package ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import pathfinder.Pathfinder
import pathfinder.PathfinderFactory
import pathfinder.PathfinderType

@Composable
fun rememberBoardState(sizeX: Int, sizeY: Int): BoardState {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    return rememberSaveable(sizeX, sizeY, density, scope, saver = BoardState.Saver(density, scope)) { BoardState(sizeX, sizeY, density, scope) }
}

class BoardState private constructor(
    val nodeCountX: Int,
    val nodeCountY: Int,
    private val scope: CoroutineScope,
    private val density: Density,
    savedNodes: List<NodeState>?
) {

    constructor(sizeX: Int, sizeY: Int, density: Density, scope: CoroutineScope) : this(sizeX, sizeY, scope, density, null)

    private var draggedNode: NodeState? = null
    private val isDraggingNode get() = draggedNode != null
    private var toggleToNodeState: NodeState? = null
    private var previousDragNodeIndex: NodeIndex = NodeIndex(-1)
    private val nodes = mutableStateListOf<NodeState>()
    private var savedNodes = mutableListOf<NodeState>()
    var pathfinderType by mutableStateOf(PathfinderType.BREADTH_FIRST)
    var isInteractionLocked by mutableStateOf(false)
        private set
    var isSearchFinished by mutableStateOf(false)

    init {
        fillNodes(savedNodes)
    }

    private fun fillNodes(savedNodes: List<NodeState>?) {
        nodes.addAll(
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

    fun onNodeClick(nodeSize: Dp, pointerPosition: Offset) {
        if (isInteractionLocked) return

        getNodeIndexFor(nodeSize, pointerPosition)?.let { nodeIndex ->
            toggleNode(nodeIndex)
        }
    }

    fun onDragStart(nodeSize: Dp, pointerPosition: Offset) {
        if (isInteractionLocked) return

        getNodeIndexFor(nodeSize, pointerPosition)?.let { nodeIndex ->
            val node = nodes[nodeIndex]
            if (node.isDraggable) {
                draggedNode = node
                previousDragNodeIndex = nodeIndex
            } else {
                toggleToNodeState = nodes[nodeIndex].toggleState
            }
        }
    }

    fun onDrag(nodeSize: Dp, pointerPosition: Offset): Boolean {
        if (isInteractionLocked) return false

        return getNodeIndexFor(nodeSize, pointerPosition)?.let { nodeIndex ->
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
            nodes[nodeIndex] = toggleToNodeState ?: checkNotNull(node.toggleState)
        }
    }

    fun onDragEnd() {
        toggleToNodeState = null
        draggedNode = null
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    fun startSearch() {
        check(!isInteractionLocked)

        savedNodes = nodes.toMutableList()
        isInteractionLocked = true
        val pathfinder = PathfinderFactory.create(pathfinderType, nodes, nodeCountX)
        ticker(ANIMATION_DELAY_MILLIS).also { ticker ->
            scope.launch {
                ticker.consumeEach { advanceAnimation(ticker, pathfinder) }
            }
        }
    }

    private fun advanceAnimation(ticker: ReceiveChannel<Unit>, pathfinder: Pathfinder) {
        val progress = pathfinder.stepForward()
        if (progress.searchFinished) {
            isSearchFinished = true
            ticker.cancel()
        }
        replaceNodes(progress.nodes)
    }

    fun removeObstacles() {
        val iterator = nodes.listIterator()
        iterator.forEach {
            if (it == NodeState.OBSTACLE) {
                iterator.set(NodeState.EMPTY)
            }
        }
    }

    fun restoreBoard() {
        replaceNodes(savedNodes)
        isSearchFinished = false
        isInteractionLocked = false
    }

    private fun replaceNodes(newNodes: List<NodeState>) {
        nodes.clear()
        nodes.addAll(newNodes)
    }

    private fun getNodeIndexFor(nodeSize: Dp, pointerPosition: Offset): NodeIndex? {
        val nodeSizePx = density.run { nodeSize.toPx() }
        val boardWidth = nodeCountX * nodeSizePx
        val boardHeight = nodeCountY * nodeSizePx

        return if (pointerPosition.x < boardWidth && pointerPosition.y < boardHeight) {
            NodeIndex(((pointerPosition.y / nodeSizePx).toInt() * nodeCountX) + (pointerPosition.x / nodeSizePx).toInt())
        } else {
            null
        }
    }

    fun getNodeStateAtPosition(x: Int, y: Int): NodeState {
        return nodes[getNodeIndex(y, x)]
    }

    private fun getNodeIndex(y: Int, x: Int) = nodeCountX * y + x

    private operator fun SnapshotStateList<NodeState>.get(index: NodeIndex): NodeState {
        return this[index.index]
    }

    private operator fun SnapshotStateList<NodeState>.set(index: NodeIndex, newValue: NodeState) {
        this[index.index] = newValue
    }

    @JvmInline
    private value class NodeIndex(val index: Int)

    companion object {
        private const val ANIMATION_DELAY_MILLIS = 5L

        @Suppress("UNCHECKED_CAST")
        fun Saver(density: Density, scope: CoroutineScope): Saver<BoardState, Any> {
            val keySizeX = "sizeX"
            val keySizeY = "sizeY"
            val keyNodes = "nodes"

            return mapSaver(
                save = { boardState ->
                    mapOf(keySizeX to boardState.nodeCountX, keySizeY to boardState.nodeCountY, keyNodes to boardState.nodes.toMutableList())
                },
                restore = { BoardState(it[keySizeX] as Int, it[keySizeY] as Int, scope, density, it[keyNodes] as List<NodeState>) }
            )
        }
    }
}