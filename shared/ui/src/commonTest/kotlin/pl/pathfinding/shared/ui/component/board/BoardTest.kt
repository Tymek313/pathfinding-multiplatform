package pl.pathfinding.shared.ui.component.board

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import pl.pathfinding.shared.ui.testutils.assertCorrectTraversableNodeCount
import pl.pathfinding.shared.ui.testutils.getBoardSizeInNodes
import pl.pathfinding.shared.ui.testutils.isDestination
import pl.pathfinding.shared.ui.testutils.isObstacle
import pl.pathfinding.shared.ui.testutils.isStart
import pl.pathfinding.shared.ui.testutils.isTraversable
import pl.pathfinding.shared.ui.testutils.performTouchInput
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BoardTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun whenTraversableNodeIsClicked_ThenItIsChangedToObstacle() = runBoardTest { board ->
        board.performTouchInput(this) {
            downOnNodeFromTopLeft(leftOffsetInNodes = 1)
            up()
        }

        board.onChildren().run {
            get(1).assert(isObstacle())
            assertCorrectTraversableNodeCount(obstacleNodeCount = 1)
        }
    }

    @Test
    fun whenPointerIsDraggedOverTraversableNodes_ThenTheyAreChangedIntoObstacles() = runBoardTest { board ->
        board.performTouchInput(this) {
            downOnNodeFromTopLeft(leftOffsetInNodes = 1)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
        }

        board.onChildren().run {
            get(1).assert(isObstacle())
            get(2).assert(isObstacle())
            get(3).assert(isObstacle())
            get(4).assert(isObstacle())
            assertCorrectTraversableNodeCount(obstacleNodeCount = 4)
        }
    }

    @Test
    fun whenPointerIsDraggedOverObstacleNodes_ThenTheyAreChangedIntoTraversable() = runBoardTest { board ->
        board.performTouchInput(this) {
            downOnNodeFromTopLeft(leftOffsetInNodes = 1)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
            up()
            downOnNodeFromTopLeft(leftOffsetInNodes = 1)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
        }

        board.onChildren().run {
            assertCorrectTraversableNodeCount(obstacleNodeCount = 0)
        }
    }

    @Test
    fun whenPointerIsDraggedOverStartNode_GivenPointerIsMovedToTraversableNode_ThenNodeIsMoved() =
        runBoardTest { board ->
            board.performTouchInput(this) {
            downOnNodeFromTopLeft(leftOffsetInNodes = 0)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
        }

        board.onChildren().run {
            get(0).assert(isTraversable())
            get(1).assert(isTraversable())
            get(2).assert(isStart())
            filter(isStart()).assertCountEquals(1)
            assertCorrectTraversableNodeCount(obstacleNodeCount = 0)
        }
    }

    @Test
    fun whenPointerIsDraggedOverStartNode_GivenPointerIsMovedToObstacleNode_ThenStartNodeIsNotMoved() =
        runBoardTest { board ->
            board.performTouchInput(this) {
                downOnNodeFromTopLeft(leftOffsetInNodes = 1)
                up()
                downOnNodeFromTopLeft(leftOffsetInNodes = 0)
                moveByNodes(xInNodes = 1)
            }

            board.onChildren().run {
                get(0).assert(isStart())
                get(1).assert(isObstacle())
                filter(isStart()).assertCountEquals(1)
                assertCorrectTraversableNodeCount(obstacleNodeCount = 1)
            }
        }

    @Test
    fun whenPointerIsDraggedOverDestinationNode_GivenPointerIsMovedToTraversableNode_ThenDestinationNodeIsMoved() =
        runBoardTest { board ->
            board.performTouchInput(this) {
                downOnNodeFromBottomRight(bottomOffsetInNodes = 0)
                moveByNodes(yInNodes = -1)
            }

            board.onChildren().run {
                onLast().assert(isTraversable())
                filter(isDestination()).assertCountEquals(1)
                get(lastChildIndex - getBoardSizeInNodes(board)).assert(isDestination())
                assertCorrectTraversableNodeCount(obstacleNodeCount = 0)
            }
        }

    @Test
    fun whenPointerIsDraggedOverDestinationNode_GivenPointerIsMovedToObstacleNode_ThenDestinationNodeIsNotMoved() =
        runBoardTest { board ->
            board.performTouchInput(this) {
                downOnNodeFromBottomRight(bottomOffsetInNodes = 1)
                up()
                downOnNodeFromBottomRight(bottomOffsetInNodes = 0)
                moveByNodes(yInNodes = -1)
            }

            board.onChildren().run {
                filterToOne(isStart()).assertIsDisplayed()
                onLast().assert(isDestination())
                filter(isDestination()).assertCountEquals(1)
                filter(isObstacle()).assertCountEquals(1)
                val nodeAboveDestinationIndex = lastChildIndex - getBoardSizeInNodes(board)
                get(nodeAboveDestinationIndex).assert(isObstacle())
                assertCorrectTraversableNodeCount(obstacleNodeCount = 1)
            }
        }

    private fun runBoardTest(block: ComposeUiTest.(board: SemanticsNodeInteraction) -> Unit) = runComposeUiTest {
        setContent { Board(rememberBoardState(), Modifier.size(200.dp)) }
        block(onNodeWithTag(TEST_TAG_BOARD))
    }
}

private val SemanticsNodeInteractionCollection.lastChildIndex
    get() = fetchSemanticsNodes().lastIndex