package pl.pathfinding.shared.domain.graph

import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import pl.pathfinding.shared.domain.node.NodeId
import kotlin.random.Random
import kotlin.test.assertEquals

class BoardTest {

    @Test(expected = IllegalArgumentException::class)
    fun `when creating board_given board size is negative_then exception should be thrown`() {
        Board(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when creating board_given board size is zero_then exception should be thrown`() {
        Board(0)
    }

    @Test
    fun `when creating board_then board contains square of board size nodes`() {
        val board = Board(7)

        assertEquals(expected = 49, actual = board.nodes.size)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the top edge_then bottom, left, right neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(1))

        assertEquals(expected = setOf(NodeId(0), NodeId(2), NodeId(4)), actual = neighbors)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the left edge_then top, right, bottom neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(3))

        assertEquals(expected = setOf(NodeId(0), NodeId(4), NodeId(6)), actual = neighbors)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the right edge_then top, left, bottom neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(5))

        assertEquals(expected = setOf(NodeId(2), NodeId(4), NodeId(8)), actual = neighbors)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the bottom edge_then top, left, right neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(7))

        assertEquals(expected = setOf(NodeId(6), NodeId(4), NodeId(8)), actual = neighbors)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the top-left corner_then right, bottom neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(0))

        assertEquals(expected = setOf(NodeId(1), NodeId(3)), actual = neighbors)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the top-right corner_then left, bottom neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(2))

        assertEquals(expected = setOf(NodeId(1), NodeId(5)), actual = neighbors)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the bottom-left corner_then top, right neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(6))

        assertEquals(expected = setOf(NodeId(3), NodeId(7)), actual = neighbors)
    }

    @Test
    fun `when obtaining neighbors_given neighbor is located at the bottom-right corner_then top, left neighbors are returned`() {
        val board = Board(3)

        val neighbors = board.getNeighbors(NodeId(8))

        assertEquals(expected = setOf(NodeId(7), NodeId(5)), actual = neighbors)
    }

    @Test
    fun `when obtaining corresponding id_given the other graph is not of the same type_then null is returned`() {
        val board = Board(3)
        val dummyGraph = mockk<Graph>()

        val nodeId = board.getCorrespondingId(NodeId(0), dummyGraph)

        assertEquals(expected = null, actual = nodeId)
    }

    @Test
    fun `when obtaining corresponding id_given the other graph is smaller and requested id lies outside the other graph on the x axis_then null is returned`() {
        val board = Board(4)
        val other = Board(3)

        val nodeId = board.getCorrespondingId(NodeId(3), other)

        assertEquals(expected = null, actual = nodeId)
    }

    @Test
    fun `when obtaining corresponding id_given the other graph is smaller and requested id lies outside the other graph on the y axis_then null is returned`() {
        val board = Board(4)
        val other = Board(3)

        val nodeId = board.getCorrespondingId(NodeId(12), other)

        assertEquals(expected = null, actual = nodeId)
    }

    @Test
    fun `when obtaining corresponding id_given the other graph is smaller and requested id exists in the other graph_then correct node id is returned`() {
        val board = Board(4)
        val other = Board(3)

        val nodeId = board.getCorrespondingId(NodeId(8), other)

        assertEquals(expected = NodeId(6), actual = nodeId)
    }

    @Test
    fun `when obtaining corresponding id_given the other graph is larger_then correct node id is returned`() {
        val board = Board(3)
        val other = Board(4)

        val nodeId = board.getCorrespondingId(NodeId(3), other)

        assertEquals(expected = NodeId(4), actual = nodeId)
    }
}