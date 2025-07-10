package pl.pathfinding.shared.ui.component.board

import pl.pathfinding.shared.domain.Node
import pl.pathfinding.shared.domain.NodeState

internal class NodeFactory {

    fun createNodes(boardSizeInNodes: Int, nodeStates: List<NodeState>?): List<Node> {
        val totalNodeCount = boardSizeInNodes * boardSizeInNodes
        val allNodesNeighbors = List(totalNodeCount) { mutableSetOf<Node>() }
        val nodes = if (nodeStates == null) {
            createNodesWithDefaultState(totalNodeCount, allNodesNeighbors)
        } else {
            createNodesWithPredefinedState(totalNodeCount, nodeStates, allNodesNeighbors)
        }

        assignNeighborsToNodes(allNodesNeighbors, boardSizeInNodes, nodes)

        return nodes
    }

    private fun createNodesWithDefaultState(
        totalNodeCount: Int,
        nodesNeighbors: List<Set<Node>>
    ) = List(totalNodeCount) { index ->
        val nodeState = when (index) {
            0 -> NodeState.START
            totalNodeCount - 1 -> NodeState.DESTINATION
            else -> NodeState.TRAVERSABLE
        }
        ObservableNode(nodeState, nodesNeighbors[index])
    }

    private fun createNodesWithPredefinedState(
        totalNodeCount: Int,
        nodeStates: List<NodeState>,
        nodesNeighbors: List<Set<Node>>
    ) = List(totalNodeCount) { index -> ObservableNode(nodeStates[index], nodesNeighbors[index]) }

    private fun assignNeighborsToNodes(nodesNeighbors: List<MutableSet<Node>>, boardSizeInNodes: Int, nodes: List<Node>) {
        nodesNeighbors.forEachIndexed { index, neighborsOfNode ->
            val nodeX = index % boardSizeInNodes

            val topNeighbor = nodes.getOrNull(index - boardSizeInNodes)
            val bottomNeighbor = nodes.getOrNull(index + boardSizeInNodes)

            val isFirstNodeInRow = nodeX == 0
            val leftNeighbor = if (isFirstNodeInRow) {
                null
            } else {
                nodes.getOrNull(index - 1)
            }

            val isLastNodeInRow = nodeX == boardSizeInNodes - 1
            val rightNeighbor = if (isLastNodeInRow) {
                null
            } else {
                nodes.getOrNull(index + 1)
            }

            neighborsOfNode.addAll(listOfNotNull(topNeighbor, bottomNeighbor, leftNeighbor, rightNeighbor))
        }
    }
}