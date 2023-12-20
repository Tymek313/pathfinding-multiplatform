package ui.component.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import pathfinding.Board
import pathfinding.Board.NodeIndex
import pathfinding.NodeState
import pathfinding.Pathfinder
import pathfinding.PathfinderFactory
import pathfinding.PathfinderType
import ui.component.board.pathfinder.ObservableBoard
import ui.component.board.pathfinder.color

@Composable
fun rememberBoardState(sizeX: Int, sizeY: Int): BoardState {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    return rememberSaveable(sizeX, sizeY, density, scope, saver = BoardState.Saver(density, scope)) { BoardState(sizeX, sizeY, scope, density) }
}

class BoardState private constructor(
    val nodeCountX: Int,
    val nodeCountY: Int,
    private val scope: CoroutineScope,
    private val density: Density,
    boardToRestore: Board?
) {

    private var draggedNodeIndex: NodeIndex? = null
    private val isDraggingNode get() = draggedNodeIndex != null
    private var toggleToNodeState: NodeState? = null
    private var previousDragNodeIndex: NodeIndex? = null
    private var board: Board by mutableStateOf(boardToRestore ?: ObservableBoard(nodeCountX, nodeCountY))
    private var savedBoard: Board? = null
    var pathfinderType by mutableStateOf(PathfinderType.BREADTH_FIRST)
    private var controlState by mutableStateOf(ControlState.IDLE)
    val isBoardIdle by derivedStateOf { controlState == ControlState.IDLE }
    val isBoardSearchFinished by derivedStateOf { controlState == ControlState.SEARCH_FINISHED }

    constructor(nodeCountX: Int, nodeCountY: Int, scope: CoroutineScope, density: Density) : this(nodeCountX, nodeCountY, scope, density, null)

    fun onNodeClick(nodeSize: Dp, pointerPosition: Offset) {
        if (controlState == ControlState.IDLE) {
            getNodeIndexFor(nodeSize, pointerPosition)?.let(::toggleNode)
        }
    }

    fun onDragStart(nodeSize: Dp, pointerPosition: Offset) {
        if (controlState == ControlState.IDLE) {
            getNodeIndexFor(nodeSize, pointerPosition)?.let { nodeIndex ->
                val node = board[nodeIndex]
                if (node.isDraggable) {
                    draggedNodeIndex = nodeIndex
                    previousDragNodeIndex = nodeIndex
                } else {
                    toggleToNodeState = board[nodeIndex].toggleState
                }
            }
        }
    }

    fun onDrag(nodeSize: Dp, pointerPosition: Offset): Boolean {
        return if (controlState == ControlState.IDLE) {
            getNodeIndexFor(nodeSize, pointerPosition)?.let { nodeIndex ->
                onNodeDrag(nodeIndex)
                true
            } ?: false
        } else {
            false
        }
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
        val draggedNodeIndex = checkNotNull(draggedNodeIndex)

        if (board[destinationNodeIndex] == NodeState.EMPTY) {
            board[destinationNodeIndex] = board[draggedNodeIndex]
            board[draggedNodeIndex] = NodeState.EMPTY
            this.draggedNodeIndex = destinationNodeIndex
        }
    }

    private fun toggleNode(nodeIndex: NodeIndex) {
        val node = board[nodeIndex]
        if (node.isToggleable) {
            board[nodeIndex] = toggleToNodeState ?: checkNotNull(node.toggleState)
        }
    }

    fun onDragEnd() {
        toggleToNodeState = null
        draggedNodeIndex = null
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    fun startSearch() {
        check(controlState == ControlState.IDLE) { "Control state is not idle: $controlState" }

        savedBoard = board.copy()
        controlState = ControlState.SEARCHING
        val pathfinder = PathfinderFactory.create(pathfinderType, board)
        ticker(ANIMATION_DELAY_MILLIS).also { ticker ->
            scope.launch {
                ticker.consumeEach { advanceAnimation(ticker, pathfinder) }
            }
        }
    }

    private fun advanceAnimation(ticker: ReceiveChannel<Unit>, pathfinder: Pathfinder) {
        val boardAfterStep = pathfinder.stepForward()
        if (pathfinder.searchFinished) {
            controlState = ControlState.SEARCH_FINISHED
            ticker.cancel()
        }
        board = boardAfterStep as ObservableBoard
    }

    fun removeObstacles() {
        board.removeObstacles()
    }

    fun restoreBoard() {
        board = checkNotNull(savedBoard).copy()
        controlState = ControlState.IDLE
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

    fun getNodeColorAtPosition(x: Int, y: Int): Color {
        return board[x, y].color
    }

    enum class ControlState {
        IDLE, SEARCHING, SEARCH_FINISHED
    }

    companion object {
        private const val ANIMATION_DELAY_MILLIS = 5L

        fun Saver(density: Density, scope: CoroutineScope): Saver<BoardState, Any> {
            val keyNodeCountX = "nodeCountX"
            val keyNodeCountY = "nodeCountY"
            val keyBoard = "board"
            val keyControlState = "controlState"
            val keySavedBoard = "savedBoard"
            val keyPathfinderType = "pathfinderType"

            return mapSaver(
                save = { boardState ->
                    mapOf(
                        keyNodeCountX to boardState.nodeCountX,
                        keyNodeCountY to boardState.nodeCountY,
                        keyBoard to Board.save(saverScope = this, boardToSave = boardState.board),
                        keySavedBoard to boardState.savedBoard?.let { savedBoard -> Board.save(saverScope = this, boardToSave = savedBoard) },
                        keyControlState to boardState.controlState,
                        keyPathfinderType to boardState.pathfinderType
                    )
                },
                restore = {
                    val controlState = it[keyControlState] as ControlState
                    val savedBoard = it[keySavedBoard]?.let { Board.restore(it) }
                    val board = Board.restore(checkNotNull(it[keyBoard]))
                    // TODO: Restore the pathfinder and continue animation after state restoration
                    val wasSearchingOnStateSave = controlState == ControlState.SEARCHING
                    val boardToRestore = if (wasSearchingOnStateSave) savedBoard else board
                    val newControlState = if (wasSearchingOnStateSave) ControlState.IDLE else controlState

                    BoardState(it[keyNodeCountX] as Int, it[keyNodeCountY] as Int, scope, density, boardToRestore).also { boardState ->
                        boardState.controlState = newControlState
                        boardState.pathfinderType = it[keyPathfinderType] as PathfinderType
                        boardState.savedBoard = savedBoard
                    }
                }
            )
        }
    }
}