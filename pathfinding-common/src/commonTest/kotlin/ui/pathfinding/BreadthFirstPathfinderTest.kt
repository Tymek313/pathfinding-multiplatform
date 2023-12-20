package ui.pathfinding

import pathfinding.Board
import pathfinding.BreadthFirstPathfinder
import pathfinding.NodeState.DESTINATION
import pathfinding.NodeState.EMPTY
import pathfinding.NodeState.OBSTACLE
import pathfinding.NodeState.PATH
import pathfinding.NodeState.QUEUED
import pathfinding.NodeState.START
import pathfinding.NodeState.VISITED
import ui.pathfinding.fake.asTestBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class BreadthFirstPathfinderTest {

    @Test
    fun `when the current node is the start one then neighboring nodes are queued`() {
        val pathfinder = BreadthFirstPathfinder(
            listOf(
                listOf(EMPTY, EMPTY, EMPTY),
                listOf(EMPTY, START, EMPTY),
                listOf(EMPTY, EMPTY, DESTINATION)
            ).asTestBoard()
        )

        val boardAfterStep = pathfinder.stepForward()

        assertEquals(
            expected = listOf(
                listOf(EMPTY, QUEUED, EMPTY),
                listOf(QUEUED, START, QUEUED),
                listOf(EMPTY, QUEUED, DESTINATION)
            ).asTestBoard(),
            actual = boardAfterStep
        )
        assertFalse(pathfinder.searchFinished)
    }

    @Test
    fun `when current node is a queued one then it is marked as visited and its neighboring nodes are queued`() {
        val pathfinder = BreadthFirstPathfinder(
            listOf(
                listOf(EMPTY, EMPTY, EMPTY, EMPTY),
                listOf(EMPTY, EMPTY, START, EMPTY),
                listOf(EMPTY, EMPTY, EMPTY, DESTINATION),
            ).asTestBoard()
        )
        pathfinder.stepForward()

        val progress = pathfinder.stepForward()

        assertEquals(
            expected = listOf(
                listOf(EMPTY, QUEUED, QUEUED, EMPTY),
                listOf(QUEUED, VISITED, START, QUEUED),
                listOf(EMPTY, QUEUED, QUEUED, DESTINATION),
            ).asTestBoard(),
            actual = progress
        )
    }

    @Test
    fun `when neighboring node is an obstacle then it is not queued`() {
        val pathfinder = BreadthFirstPathfinder(
            listOf(
                listOf(EMPTY, EMPTY, EMPTY),
                listOf(OBSTACLE, START, OBSTACLE),
                listOf(EMPTY, EMPTY, DESTINATION),
            ).asTestBoard()
        )

        val progress = pathfinder.stepForward()

        assertEquals(
            expected = listOf(
                listOf(EMPTY, QUEUED, EMPTY),
                listOf(OBSTACLE, START, OBSTACLE),
                listOf(EMPTY, QUEUED, DESTINATION),
            ).asTestBoard(),
            actual = progress
        )
    }

    @Test
    fun `when current node is a destination one then mark the shortest path back to the start node and search is finished`() {
        val pathfinder = BreadthFirstPathfinder(
            listOf(
                listOf(START, EMPTY, EMPTY),
                listOf(EMPTY, EMPTY, EMPTY),
                listOf(EMPTY, EMPTY, DESTINATION),
            ).asTestBoard()
        )

        var stepBoard: Board? = null
        repeat(9) { stepBoard = pathfinder.stepForward() }

        assertEquals(
            expected = listOf(
                listOf(START, PATH, PATH),
                listOf(VISITED, VISITED, PATH),
                listOf(VISITED, VISITED, DESTINATION),
            ).asTestBoard(),
            actual = stepBoard
        )
    }

    @Test
    fun `when all possible nodes have been visited without destination then search is finished`() {
        val pathfinder = BreadthFirstPathfinder(
            listOf(
                listOf(START, EMPTY),
                listOf(EMPTY, OBSTACLE),
                listOf(OBSTACLE, DESTINATION)
            ).asTestBoard()
        )

        var progress: Board? = null
        repeat(3) { progress = pathfinder.stepForward() }

        assertEquals(
            expected = listOf(
                listOf(START, VISITED),
                listOf(VISITED, OBSTACLE),
                listOf(OBSTACLE, DESTINATION)
            ).asTestBoard(),
            actual = progress
        )
    }
}