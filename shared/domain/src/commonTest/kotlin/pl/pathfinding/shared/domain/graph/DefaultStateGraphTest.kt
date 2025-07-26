package pl.pathfinding.shared.domain.graph

import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import pl.pathfinding.shared.domain.node.NodeId
import pl.pathfinding.shared.domain.node.NodeState
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultStateGraphTest {

    private lateinit var fakeOriginalGraph: Graph
    private lateinit var fakePreviousGraph: StateGraph

    @Before
    fun setup() {
        fakeOriginalGraph = mockk()
        fakePreviousGraph = mockk()
        every { fakePreviousGraph.originalGraph } returns mockk()
    }

    @Test
    fun `when creating graph_given previous graph and corresponding start node_then start node id the one from the previous graph`() {
        originalGraphReturnsNodes()
        allNodeIdsCorrespondToThePreviousGraph()
        previousNodesAre(NodeState.TRAVERSABLE, NodeState.START, NodeState.OBSTACLE, NodeState.DESTINATION)

        val graph = DefaultStateGraph(fakeOriginalGraph, fakePreviousGraph)

        assertEquals(expected = NodeId(1), actual = graph.startNodeId)
    }

    @Test
    fun `when creating graph_given previous graph and no corresponding start node_then start node id is the first node`() {
        originalGraphReturnsNodes()
        allNodeIdsCorrespondToThePreviousGraph()
        allPreviousNodesAreTraversable()

        val graph = DefaultStateGraph(fakeOriginalGraph, fakePreviousGraph)

        assertEquals(expected = NodeId(0), actual = graph.startNodeId)
    }

    private fun allNodeIdsCorrespondToThePreviousGraph() {
        every { fakeOriginalGraph.getCorrespondingId(any(), any()) } answers { NodeId(call.invocation.args[0] as Int) }
    }

    @Test
    fun `when creating graph_given previous graph and no corresponding destination node_then destination node id is the last node`() {
        originalGraphReturnsNodes()
        noCorrespondingIdsInPreviousGraph()
        allPreviousNodesAreTraversable()

        val graph = DefaultStateGraph(fakeOriginalGraph, fakePreviousGraph)

        assertEquals(
            expected = NodeId(3),
            actual = graph.nodeStates.firstNotNullOf { (id, state) ->
                if (state == NodeState.DESTINATION) id else null
            }
        )
    }

    @Test
    fun `when creating graph_given previous graph and no corresponding node_then node is traversable`() {
        originalGraphReturnsNodes()
        noCorrespondingIdsInPreviousGraph()
        allPreviousNodesAreTraversable()

        val graph = DefaultStateGraph(fakeOriginalGraph, fakePreviousGraph)

        assertEquals(expected = NodeState.TRAVERSABLE, actual = graph.nodeStates[NodeId(1)])
    }

    @Test
    fun `when creating graph_given no previous graph_then start node id is the first node in the graph`() {
        originalGraphReturnsNodes()

        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        assertEquals(expected = NodeId(0), actual = graph.startNodeId)
    }

    @Test
    fun `when creating graph_given no previous graph_then destination node id is the last node in the graph`() {
        originalGraphReturnsNodes()

        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        assertEquals(
            expected = NodeId(3),
            actual = graph.nodeStates.firstNotNullOf { (id, state) ->
                if (state == NodeState.DESTINATION) id else null
            }
        )
    }

    @Test
    fun `when creating graph_given no previous graph_then default node state is traversable`() {
        originalGraphReturnsNodes()

        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        assertEquals(
            expected = listOf(NodeState.TRAVERSABLE, NodeState.TRAVERSABLE),
            actual = graph.nodeStates
                .values
                .toList()
                .subList(1, 3)
        )
    }

    @Test
    fun `when getting node state for node id_then correct node state is returned`() {
        originalGraphReturnsNodes()
        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        assertEquals(expected = graph[NodeId(0)], actual = NodeState.START)
        assertEquals(expected = graph[NodeId(1)], actual = NodeState.TRAVERSABLE)
        assertEquals(expected = graph[NodeId(2)], actual = NodeState.TRAVERSABLE)
        assertEquals(expected = graph[NodeId(3)], actual = NodeState.DESTINATION)
    }

    @Test
    fun `when setting node state for node id_then correct node state is set for the id`() {
        originalGraphReturnsNodes()
        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        graph[NodeId(0)] = NodeState.DESTINATION
        graph[NodeId(1)] = NodeState.START
        graph[NodeId(2)] = NodeState.TRAVERSABLE
        graph[NodeId(3)] = NodeState.OBSTACLE

        assertEquals(expected = NodeState.DESTINATION, actual = graph.nodeStates[NodeId(0)])
        assertEquals(expected = NodeState.START, actual = graph.nodeStates[NodeId(1)])
        assertEquals(expected = NodeState.TRAVERSABLE, actual = graph.nodeStates[NodeId(2)])
        assertEquals(expected = NodeState.OBSTACLE, actual = graph.nodeStates[NodeId(3)])
    }

    @Test
    fun `when swapping node states_then node states at given positions are swapped`() {
        every { fakeOriginalGraph.nodes } returns listOf(NodeId(0), NodeId(1), NodeId(2), NodeId(3))
        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        graph.swapStates(NodeId(0), NodeId(1))
        graph.swapStates(NodeId(0), NodeId(2))
        graph.swapStates(NodeId(3), NodeId(1))

        assertEquals(
            expected = mapOf(
                NodeId(0) to NodeState.TRAVERSABLE,
                NodeId(1) to NodeState.DESTINATION,
                NodeId(2) to NodeState.TRAVERSABLE,
                NodeId(3) to NodeState.START,
            ),
            actual = graph.nodeStates,
        )
    }

    @Test
    fun `when removing obstacles_then only obstacles are replaced with traversable states`() {
        originalGraphReturnsNodes()
        val graph = DefaultStateGraph(fakeOriginalGraph, null)
        graph[NodeId(1)] = NodeState.OBSTACLE

        graph.removeAllObstacles()

        assertEquals(
            expected = mapOf(
                NodeId(0) to NodeState.START,
                NodeId(1) to NodeState.TRAVERSABLE,
                NodeId(2) to NodeState.TRAVERSABLE,
                NodeId(3) to NodeState.DESTINATION,
            ),
            actual = graph.nodeStates,
        )
    }

    @Test
    fun `when restoring snapshot_then graph contains state from the time of snapshot creation`() {
        originalGraphReturnsNodes()
        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        graph[NodeId(0)] = NodeState.OBSTACLE
        val snapshot = graph.createSnapshot()
        graph[NodeId(1)] = NodeState.OBSTACLE
        graph.restoreFromSnapshot(snapshot)

        assertEquals(
            expected = mapOf(
                NodeId(0) to NodeState.OBSTACLE,
                NodeId(1) to NodeState.TRAVERSABLE,
                NodeId(2) to NodeState.TRAVERSABLE,
                NodeId(3) to NodeState.DESTINATION,
            ),
            actual = graph.nodeStates,
        )
    }

    @Test
    fun `when restoring snapshot from serialized form_then graph contains state from the time of snapshot creation`() {
        originalGraphReturnsNodes()
        val graph = DefaultStateGraph(fakeOriginalGraph, null)

        graph[NodeId(0)] = NodeState.OBSTACLE
        val serialized = graph.createSnapshot().serialize()
        graph[NodeId(1)] = NodeState.OBSTACLE

        graph.restoreFromSnapshot(
            DefaultStateGraph.Snapshot.createFromSerialized(serialized)
        )

        assertEquals(
            expected = mapOf(
                NodeId(0) to NodeState.OBSTACLE,
                NodeId(1) to NodeState.TRAVERSABLE,
                NodeId(2) to NodeState.TRAVERSABLE,
                NodeId(3) to NodeState.DESTINATION,
            ),
            actual = graph.nodeStates,
        )
    }

    @Suppress("SameParameterValue")
    private fun previousNodesAre(vararg states: NodeState) =
        every { fakePreviousGraph[any()] } answers { states[firstArg<Int>()] }

    private fun allPreviousNodesAreTraversable() = every { fakePreviousGraph[any()] } returns NodeState.TRAVERSABLE

    private fun noCorrespondingIdsInPreviousGraph() =
        every { fakeOriginalGraph.getCorrespondingId(any(), any()) } returns null

    private fun originalGraphReturnsNodes() =
        every { fakeOriginalGraph.nodes } returns listOf(NodeId(0), NodeId(1), NodeId(2), NodeId(3))
}