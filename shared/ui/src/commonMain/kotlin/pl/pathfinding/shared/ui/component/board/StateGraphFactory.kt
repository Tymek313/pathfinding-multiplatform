package pl.pathfinding.shared.ui.component.board

import pl.pathfinding.shared.domain.graph.Board
import pl.pathfinding.shared.domain.graph.DefaultStateGraph
import pl.pathfinding.shared.domain.graph.StateGraph

internal interface StateGraphFactory {
    fun create(graphSizeInNodes: Int, previousGraph: StateGraph?): StateGraph
}

internal object DefaultStateGraphFactory : StateGraphFactory {
    override fun create(graphSizeInNodes: Int, previousGraph: StateGraph?): StateGraph = DefaultStateGraph(
        originalGraph = Board(graphSizeInNodes),
        previousGraph = previousGraph
    )
}
