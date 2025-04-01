package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import pl.pathfinding.shared.pathfinder.Node
import pl.pathfinding.shared.pathfinder.NodeState

internal class ObservableNode(
    initialState: NodeState,
    override val neighbors: Set<Node>
): Node {
    override var state by mutableStateOf(initialState)
}