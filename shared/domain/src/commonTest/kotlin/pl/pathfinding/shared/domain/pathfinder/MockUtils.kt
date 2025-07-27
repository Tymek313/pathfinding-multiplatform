package pl.pathfinding.shared.domain.pathfinder

import io.mockk.MockKStubScope
import pl.pathfinding.shared.domain.node.NodeId

fun MockKStubScope<Set<NodeId>, Set<NodeId>>.are(vararg neighbors: Int) =
    returns(neighbors.map(::NodeId).toSet())