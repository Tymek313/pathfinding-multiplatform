package pl.pathfinding.shared.ui.testutils

import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.filter

fun SemanticsNodeInteractionCollection.assertCorrectTraversableNodeCount(obstacleNodeCount: Int) {
    require(obstacleNodeCount >= 0)
    val childCount = fetchSemanticsNodes().size
    val traversableNodeCount = childCount - obstacleNodeCount - 2 // Start and destination nodes
    filter(isTraversable()).assertCountEquals(traversableNodeCount)
}