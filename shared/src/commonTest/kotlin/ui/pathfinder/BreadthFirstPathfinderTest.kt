package ui.pathfinder

import pathfinder.BreadthFirstPathfinder
import pathfinder.Pathfinder
import ui.component.board.NodeState.DESTINATION
import ui.component.board.NodeState.EMPTY
import ui.component.board.NodeState.OBSTACLE
import ui.component.board.NodeState.PATH
import ui.component.board.NodeState.QUEUED
import ui.component.board.NodeState.START
import ui.component.board.NodeState.VISITED
import kotlin.test.Test
import kotlin.test.assertEquals

class BreadthFirstPathfinderTest {

    @Test
    fun `when current node is the start one then neighboring nodes are queued`() {
        val pathfinder = BreadthFirstPathfinder(
            nodes = listOf(
                EMPTY, EMPTY, EMPTY,
                EMPTY, START, EMPTY,
                EMPTY, EMPTY, DESTINATION,
            ),
            rowSize = 3
        )

        val progress = pathfinder.stepForward()

        assertEquals(
            expected = Pathfinder.Progress(
                nodes = listOf(
                    EMPTY, QUEUED, EMPTY,
                    QUEUED, START, QUEUED,
                    EMPTY, QUEUED, DESTINATION,
                ),
                searchFinished = false
            ),
            actual = progress
        )
    }

    @Test
    fun `when current node is a queued one then it is marked as visited and its neighboring nodes are queued`() {
        val pathfinder = BreadthFirstPathfinder(
            nodes = listOf(
                EMPTY, EMPTY, EMPTY, EMPTY,
                EMPTY, EMPTY, START, EMPTY,
                EMPTY, EMPTY, EMPTY, DESTINATION,
            ),
            rowSize = 4
        )
        pathfinder.stepForward()

        val progress = pathfinder.stepForward()

        assertEquals(
            expected = Pathfinder.Progress(
                nodes = listOf(
                    EMPTY, QUEUED, QUEUED, EMPTY,
                    QUEUED, VISITED, START, QUEUED,
                    EMPTY, QUEUED, QUEUED, DESTINATION,
                ),
                searchFinished = false
            ),
            actual = progress
        )
    }

    @Test
    fun `when neighboring node is an obstacle then it is not queued`() {
        val pathfinder = BreadthFirstPathfinder(
            nodes = listOf(
                EMPTY, EMPTY, EMPTY,
                OBSTACLE, START, OBSTACLE,
                EMPTY, EMPTY, DESTINATION,
            ),
            rowSize = 3
        )

        val progress = pathfinder.stepForward()

        assertEquals(
            expected = Pathfinder.Progress(
                nodes = listOf(
                    EMPTY, QUEUED, EMPTY,
                    OBSTACLE, START, OBSTACLE,
                    EMPTY, QUEUED, DESTINATION,
                ),
                searchFinished = false
            ),
            actual = progress
        )
    }

    @Test
    fun `when current node is a destination one then mark the shortest path back to the start node and search is finished`() {
        val pathfinder = BreadthFirstPathfinder(
            nodes = listOf(
                START, EMPTY, EMPTY,
                EMPTY, EMPTY, EMPTY,
                EMPTY, EMPTY, DESTINATION,
            ),
            rowSize = 3
        )

        var progress: Pathfinder.Progress? = null
        repeat(9) { progress = pathfinder.stepForward() }

        assertEquals(
            expected = Pathfinder.Progress(
                nodes = listOf(
                    START, PATH, PATH,
                    VISITED, VISITED, PATH,
                    VISITED, VISITED, DESTINATION,
                ),
                searchFinished = true
            ),
            actual = progress
        )
    }

    @Test
    fun `when all possible nodes have been visited without destination then search is finished`() {
        val pathfinder = BreadthFirstPathfinder(
            nodes = listOf(
                START, EMPTY,
                EMPTY, OBSTACLE,
                OBSTACLE, DESTINATION
            ),
            rowSize = 2
        )

        var progress: Pathfinder.Progress? = null
        repeat(3) { progress = pathfinder.stepForward() }

        assertEquals(
            expected = Pathfinder.Progress(
                nodes = listOf(
                    START, VISITED,
                    VISITED, OBSTACLE,
                    OBSTACLE, DESTINATION
                ),
                searchFinished = true
            ),
            actual = progress
        )
    }
}