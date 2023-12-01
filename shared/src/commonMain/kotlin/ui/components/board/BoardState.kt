package ui.components.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import ui.pathfinder.PathfinderFactory
import ui.pathfinder.PathfinderType

@Composable
fun rememberBoardState(sizeX: Int, sizeY: Int) = remember(sizeX, sizeY) { BoardState(sizeX, sizeY) }

class BoardState(val sizeX: Int, val sizeY: Int) {
    private val nodeDisplaySize = 20
    val nodeDisplaySizeDp = nodeDisplaySize.dp
    private val boardSize = IntSize(sizeX * nodeDisplaySize, sizeY * nodeDisplaySize)
    private var draggedNode: NodeState? = null
    private val isDraggingNode get() = draggedNode != null
    private var toggleToNodeState: NodeState? = null
    private var previousDragNodeIndex: NodeIndex = NodeIndex(-1)
    private val nodes = mutableStateListOf<NodeState>().apply(::fillBoard)
    private val nodesMap = mutableStateListOf<NodeState>()
    private var pathfinder by mutableStateOf(PathfinderType.BREADTH_FIRST)

    private fun fillBoard(nodes: SnapshotStateList<NodeState>) {
        val startPosition = getNodeIndex(x = 0, y = 0)
        val endPosition = getNodeIndex(x = sizeX - 1, y = sizeY - 1)
        nodes.addAll(
            (0..<sizeX * sizeY).map { index ->
                when (index) {
                    startPosition -> NodeState.START
                    endPosition -> NodeState.DESTINATION
                    else -> NodeState.EMPTY
                }
            }
        )
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

    fun onDrag(pointerPosition: Offset) {
        getNodeIndexFor(pointerPosition)?.let { nodeIndex ->
            if (nodeIndex != previousDragNodeIndex) {
                if (isDraggingNode) {
                    moveNode(nodeIndex)
                } else {
                    toggleNode(nodeIndex)
                    previousDragNodeIndex = nodeIndex
                }
            }
        }
    }

    fun onDragEnd() {
        draggedNode = null
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun startSearch() {
        nodesMap.setAll(nodes)
        val pathfinder = PathfinderFactory.create(pathfinder, nodes, sizeX)
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
            NodeIndex((pointerPosition.y.toInt() / nodeDisplaySize * sizeX) + (pointerPosition.x.toInt() / nodeDisplaySize))
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

    private fun getNodeIndex(y: Int, x: Int) = sizeX * y + x

    fun getNodeStateAtPosition(x: Int, y: Int): NodeState {
        return nodes[getNodeIndex(y, x)]
    }

    private operator fun SnapshotStateList<NodeState>.get(index: NodeIndex): NodeState {
        return this[index.index]
    }

    private operator fun SnapshotStateList<NodeState>.set(index: NodeIndex, newValue: NodeState) {
        this[index.index] = newValue
    }

    @JvmInline
    private value class NodeIndex(val index: Int)
}