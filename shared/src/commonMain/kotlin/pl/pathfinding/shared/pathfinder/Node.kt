package pl.pathfinding.shared.pathfinder

internal interface Node {
    val neighbors: Set<Node>
    var state: NodeState
}