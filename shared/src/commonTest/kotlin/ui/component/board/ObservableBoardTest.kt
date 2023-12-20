package ui.component.board

import pathfinder.Board.NodeIndex
import pathfinder.NodeState
import ui.component.board.pathfinder.ObservableBoard
import kotlin.test.Test
import kotlin.test.assertEquals

class ObservableBoardTest {

    @Test
    fun `generates initial nodes with start and destination nodes at the opposite corners`() {
        val board = ObservableBoard(sizeX = 2, sizeY = 2)

        assertEquals(expected = NodeState.START, actual = board[0, 0])
        assertEquals(expected = NodeState.EMPTY, actual = board[1, 0])
        assertEquals(expected = NodeState.EMPTY, actual = board[0, 1])
        assertEquals(expected = NodeState.DESTINATION, actual = board[1, 1])
    }

    @Test
    fun `removes obstacle nodes`() {
        val board = ObservableBoard(sizeX = 2, sizeY = 2)
        board[NodeIndex(1)] = NodeState.OBSTACLE

        board.removeObstacles()

        assertEquals(expected = NodeState.START, actual = board[NodeIndex(0)])
        assertEquals(expected = NodeState.EMPTY, actual = board[NodeIndex(1)])
        assertEquals(expected = NodeState.EMPTY, actual = board[NodeIndex(2)])
        assertEquals(expected = NodeState.DESTINATION, actual = board[NodeIndex(3)])
    }

    @Test
    fun `returns all 4 neighboring nodes for node in the middle of the board`() {
        val board = ObservableBoard(sizeX = 3, sizeY = 3)

        val neighbors = board.getNeighborsFor(NodeIndex(4))

        assertEquals(
            expected = createNodeIndices(1, 3, 5, 7),
            actual = neighbors
        )
    }

    @Test
    fun `returns neighboring nodes for edge nodes (top-left corner)`() {
        val board = ObservableBoard(sizeX = 2, sizeY = 2)

        val neighbors = board.getNeighborsFor(NodeIndex(0))

        assertEquals(
            expected = createNodeIndices(1, 2),
            actual = neighbors
        )
    }

    @Test
    fun `returns neighboring nodes for edge nodes (bottom-right corner)`() {
        val board = ObservableBoard(sizeX = 2, sizeY = 2)

        val neighbors = board.getNeighborsFor(NodeIndex(3))

        assertEquals(
            expected = createNodeIndices(1, 2),
            actual = neighbors
        )
    }

    @Test
    fun `replaces node at the specified index`() {
        val board = ObservableBoard(sizeX = 2, sizeY = 2)

        board[NodeIndex(0)] = NodeState.OBSTACLE

        assertEquals(expected = board[NodeIndex(0)], actual = NodeState.OBSTACLE)
    }

    private fun createNodeIndices(vararg indices: Int): Set<NodeIndex> {
        return indices.map { NodeIndex(it) }.toSet()
    }
}