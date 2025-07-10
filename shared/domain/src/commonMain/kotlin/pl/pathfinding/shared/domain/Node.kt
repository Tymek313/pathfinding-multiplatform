package pl.pathfinding.shared.domain

interface Node {
    val neighbors: Set<Node>
    var state: NodeState
}