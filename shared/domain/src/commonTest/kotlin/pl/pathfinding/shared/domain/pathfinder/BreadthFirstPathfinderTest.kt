package pl.pathfinding.shared.domain.pathfinder

import io.mockk.MockKStubScope
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import pl.pathfinding.shared.domain.graph.StateGraph
import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState
import kotlin.test.Test
import kotlin.test.assertEquals

class BreadthFirstPathfinderTest {

    private lateinit var fakeGraph: StateGraph
    private lateinit var testNodeStates: MutableMap<NodeId, NodeState>

    @Before
    fun setup() {
        fakeGraph = mockk()
        every { fakeGraph[any()] } answers { testNodeStates[NodeId(firstArg<Int>())]!! }
        every { fakeGraph[any()] = any() } answers { testNodeStates[NodeId(firstArg<Int>())] = secondArg<NodeState>() }
        every { fakeGraph.nodeStates } answers { testNodeStates }
    }

    @Test
    fun `when search is advanced_given current node is the start node_then queueable neighboring nodes are marked as queued`() {
        testNodeStates = createNodeStates(
            0 to NodeState.TRAVERSABLE,
            1 to NodeState.TRAVERSABLE,
            2 to NodeState.START,
            3 to NodeState.TRAVERSABLE,
            4 to NodeState.TRAVERSABLE,
        )
        neighborsOf(2).are(0, 3)
        val pathfinder = createPathfinder()

        pathfinder.advance()

        assertEquals(
            expected = createNodeStates(
                0 to NodeState.QUEUED,
                1 to NodeState.TRAVERSABLE,
                2 to NodeState.START,
                3 to NodeState.QUEUED,
                4 to NodeState.TRAVERSABLE,
            ),
            actual = testNodeStates
        )
    }

    @Test
    fun `when search is advanced_given current node is a queued node_then node is marked as visited and queueable neighboring nodes are marked as queued`() {
        testNodeStates = createNodeStates(
            0 to NodeState.TRAVERSABLE,
            1 to NodeState.OBSTACLE,
            2 to NodeState.START,
            3 to NodeState.TRAVERSABLE,
            4 to NodeState.VISITED,
            5 to NodeState.TRAVERSABLE,
        )
        neighborsOf(2).are(5, 0)
        neighborsOf(5).are(0, 1, 2, 3, 4)
        val pathfinder = createPathfinder()
        pathfinder.advance()

        pathfinder.advance()

        assertEquals(
            expected = createNodeStates(
                0 to NodeState.QUEUED,
                1 to NodeState.OBSTACLE,
                2 to NodeState.START,
                3 to NodeState.QUEUED,
                4 to NodeState.VISITED,
                5 to NodeState.VISITED,
            ),
            actual = testNodeStates
        )
    }

    @Test
    fun `when search is advanced_given neighboring node is the destination node_then path is marked`() {
        testNodeStates = createNodeStates(
            0 to NodeState.START,
            1 to NodeState.OBSTACLE,
            2 to NodeState.DESTINATION,
            3 to NodeState.TRAVERSABLE,
            4 to NodeState.TRAVERSABLE,
            5 to NodeState.TRAVERSABLE,
        )
        neighborsOf(0).are(1, 3)
        neighborsOf(3).are(4, 0)
        neighborsOf(4).are(3, 5, 1)
        neighborsOf(5).are(4, 2)
        val pathfinder = createPathfinder()

        pathfinder.advance()
        pathfinder.advance()
        pathfinder.advance()
        pathfinder.advance()

        assertEquals(
            expected = createNodeStates(
                0 to NodeState.START,
                1 to NodeState.OBSTACLE,
                2 to NodeState.DESTINATION,
                3 to NodeState.PATH,
                4 to NodeState.PATH,
                5 to NodeState.PATH,
            ),
            actual = testNodeStates
        )
    }

    @Test
    fun `when search is advanced_given current node is a queued one_then next visited is the earliest previously encountered and unvisited`() {
        testNodeStates = createNodeStates(
            0 to NodeState.START,
            1 to NodeState.TRAVERSABLE,
            2 to NodeState.TRAVERSABLE,
            3 to NodeState.TRAVERSABLE,
            4 to NodeState.TRAVERSABLE,
        )
        neighborsOf(0).are(1, 2)
        neighborsOf(1).are(3)
        neighborsOf(2).are(4)
        val pathfinder = createPathfinder()

        pathfinder.advance()
        pathfinder.advance()
        pathfinder.advance()

        assertEquals(
            expected = createNodeStates(
                0 to NodeState.START,
                1 to NodeState.VISITED,
                2 to NodeState.VISITED,
                3 to NodeState.QUEUED,
                4 to NodeState.QUEUED,
            ),
            actual = testNodeStates
        )
    }

    private fun createNodeStates(vararg idsToStates: Pair<Int, NodeState>) =
        idsToStates.toMap().mapKeys { NodeId(it.key) }.toMutableMap()

    private fun createPathfinder(): BreadthFirstPathfinder = BreadthFirstPathfinder(fakeGraph)

    private fun neighborsOf(idIndex: Int) = every { fakeGraph.getNeighbors(NodeId(idIndex)) }
}