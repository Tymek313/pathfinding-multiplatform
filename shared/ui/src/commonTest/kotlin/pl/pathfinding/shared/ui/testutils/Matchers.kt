package pl.pathfinding.shared.ui.testutils

import androidx.compose.ui.test.hasStateDescription

fun isTraversable() = hasStateDescription(traversableNodeString)

fun isStart() = hasStateDescription(startNodeString)

fun isPath() = hasStateDescription(pathNodeString)

fun isQueued() = hasStateDescription(queuedNodeString)

fun isVisited() = hasStateDescription(visitedNodeString)

fun isDestination() = hasStateDescription(destinationNodeString)

fun isObstacle() = hasStateDescription(obstacleNodeString)