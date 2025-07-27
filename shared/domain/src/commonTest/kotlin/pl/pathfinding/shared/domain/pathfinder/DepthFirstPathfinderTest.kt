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

class DepthFirstPathfinderTest {

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
    fun `when search is advanced_given current node is a queued one_then next visited is the latest previously encountered and unvisited`() {
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
        neighborsOf(4).are()
        val pathfinder = DepthFirstPathfinder(fakeGraph)

        pathfinder.advance()
        pathfinder.advance()
        pathfinder.advance()

        assertEquals(
            expected = createNodeStates(
                0 to NodeState.START,
                1 to NodeState.QUEUED,
                2 to NodeState.VISITED,
                3 to NodeState.TRAVERSABLE,
                4 to NodeState.VISITED,
            ),
            actual = testNodeStates
        )
    }

    private fun createNodeStates(vararg idsToStates: Pair<Int, NodeState>) =
        idsToStates.toMap().mapKeys { NodeId(it.key) }.toMutableMap()

    private fun neighborsOf(idIndex: Int) = every { fakeGraph.getNeighbors(NodeId(idIndex)) }
}