package pl.pathfinding.shared.ui.testutils

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import pathfinding.shared.ui.generated.resources.Res
import pathfinding.shared.ui.generated.resources.breadth_first
import pathfinding.shared.ui.generated.resources.node_state_destination
import pathfinding.shared.ui.generated.resources.node_state_obstacle
import pathfinding.shared.ui.generated.resources.node_state_path
import pathfinding.shared.ui.generated.resources.node_state_queued
import pathfinding.shared.ui.generated.resources.node_state_start
import pathfinding.shared.ui.generated.resources.node_state_traversable
import pathfinding.shared.ui.generated.resources.node_state_visited
import pathfinding.shared.ui.generated.resources.pathfinding_algorithm
import pathfinding.shared.ui.generated.resources.restore_board
import org.jetbrains.compose.resources.getString as getStringAsync

fun getString(res: StringResource) = runBlocking { getStringAsync(res) }

val traversableNodeString get() = getString(Res.string.node_state_traversable)
val startNodeString get() = getString(Res.string.node_state_start)
val obstacleNodeString get() = getString(Res.string.node_state_obstacle)
val pathNodeString get() = getString(Res.string.node_state_path)
val visitedNodeString get() = getString(Res.string.node_state_visited)
val queuedNodeString get() = getString(Res.string.node_state_queued)
val destinationNodeString get() = getString(Res.string.node_state_destination)
val breadthFirstString get() = getString(Res.string.breadth_first)
val restoreBoardString get() = getString(Res.string.restore_board)
val pathfindingAlgorithmString get() = getString(Res.string.pathfinding_algorithm)
