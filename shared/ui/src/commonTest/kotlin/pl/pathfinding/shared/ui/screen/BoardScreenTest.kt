@file:OptIn(ExperimentalTestApi::class)

package pl.pathfinding.shared.ui.screen

import androidx.compose.foundation.layout.size
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.test.waitUntilNodeCount
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import pathfinding.shared.ui.generated.resources.Res
import pathfinding.shared.ui.generated.resources.depth_first
import pathfinding.shared.ui.generated.resources.pathfinding_algorithm
import pathfinding.shared.ui.generated.resources.remove_obstacles
import pathfinding.shared.ui.generated.resources.start_search
import pl.pathfinding.shared.ui.component.board.TEST_TAG_BOARD
import pl.pathfinding.shared.ui.testutils.assertCorrectTraversableNodeCount
import pl.pathfinding.shared.ui.testutils.breadthFirstString
import pl.pathfinding.shared.ui.testutils.getBoardSizeInNodes
import pl.pathfinding.shared.ui.testutils.getString
import pl.pathfinding.shared.ui.testutils.isDestination
import pl.pathfinding.shared.ui.testutils.isObstacle
import pl.pathfinding.shared.ui.testutils.isPath
import pl.pathfinding.shared.ui.testutils.isQueued
import pl.pathfinding.shared.ui.testutils.isStart
import pl.pathfinding.shared.ui.testutils.isVisited
import pl.pathfinding.shared.ui.testutils.pathfindingAlgorithmString
import pl.pathfinding.shared.ui.testutils.performTouchInput
import pl.pathfinding.shared.ui.testutils.restoreBoardString
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BoardScreenTest {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun whenScreenIsCreated_GivenWindowIsExpanded_ThenBoardWithControlsAreVisible() = runComposeUiTest {
        setContent {
            BoardScreen(
                WindowSizeClass.calculateFromSize(
                    size = DpSize(0.dp, 0.dp),
                    supportedWidthSizeClasses = setOf(WindowWidthSizeClass.Expanded)
                )
            )
        }

        onNodeWithTag(TEST_TAG_BOARD).assertIsDisplayed()
        onNodeWithText(pathfindingAlgorithmString).assertIsDisplayed()
        onBreadthFirstButton().assertIsDisplayed()
        onStartSearchButton().assertIsDisplayed()
        onRestoreBoardButton().assertIsDisplayed()
        onRemoveObstaclesButton().assertIsDisplayed()
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun whenScreenIsCreated_GivenWindowIsCompact_ThenBoardWithControlsAreVisible() = runComposeUiTest {
        setContent {
            BoardScreen(
                WindowSizeClass.calculateFromSize(
                    size = DpSize(0.dp, 0.dp),
                    supportedWidthSizeClasses = setOf(WindowWidthSizeClass.Compact),
                    supportedHeightSizeClasses = setOf(WindowHeightSizeClass.Expanded)
                )
            )
        }

        onNodeWithTag(TEST_TAG_BOARD).assertIsDisplayed()
        onNodeWithText(pathfindingAlgorithmString).assertIsDisplayed()
        onBreadthFirstButton().assertIsDisplayed()
        onStartSearchButton().assertIsDisplayed()
        onRestoreBoardButton().assertIsDisplayed()
        onRemoveObstaclesButton().assertIsDisplayed()
    }

    @Test
    fun whenPathfinderDropdownIsClicked_ThenBreadthAndDepthAlgorithmsOptionsAreShown() = runComposeUiTest {
        setSmallBoardScreenAsContent()

        onBreadthFirstButton().performClick()

        onDepthFirstButton().assertIsDisplayed()
        onAllNodesWithText(breadthFirstString).assertCountEquals(2).apply {
            get(0).assertIsDisplayed()
            get(1).assertIsDisplayed()
        }
    }

    @Test
    fun whenPathfinderDropdownOptionIsClicked_ThenDropdownOptionsDisappear() = runComposeUiTest {
        setSmallBoardScreenAsContent()
        onBreadthFirstButton().performClick()

        onDepthFirstButton().performClick()

        onAllNodesWithText(breadthFirstString).assertCountEquals(0)
    }

    @Test
    fun whenRemoveObstaclesButtonIsClicked_ThenAllObstaclesAreCleared() = runComposeUiTest {
        setSmallBoardScreenAsContent()
        val board = onNodeWithTag(TEST_TAG_BOARD)
        board.performTouchInput(this) {
            downOnNodeFromTopLeft(leftOffsetInNodes = 1)
            moveByNodes(xInNodes = 1)
            moveByNodes(xInNodes = 1)
            up()
        }

        onRemoveObstaclesButton().performClick()
        waitUntilDoesNotExist(isObstacle())

        board.onChildren().assertCorrectTraversableNodeCount(obstacleNodeCount = 0)
    }

    @Test
    fun whenRestoreBoardButtonIsClicked_ThenBoardIsBroughtToTheStateFromBeforePathfinding() = runComposeUiTest {
        setSmallBoardScreenAsContent()
        onStartSearchButton().performClick()
        waitUntilAtLeastOneExists(isPath())
        val board = onNodeWithTag(TEST_TAG_BOARD)

        onRestoreBoardButton().performClick()

        board.onChildren().apply {
            filter(isPath()).assertCountEquals(0)
            filter(isVisited()).assertCountEquals(0)
            get(0).assert(isStart())
            onLast().assert(isDestination())
        }
    }

    @Test
    fun whenSearchIsInProgress_ThenAllControlsAreDisabled() = runComposeUiTest {
        setSmallBoardScreenAsContent()

        mainClock.autoAdvance = false // Prevent traversing the entire graph before assertions
        onStartSearchButton().performClick()
        mainClock.advanceTimeByFrame()
        mainClock.advanceTimeByFrame()

        onStartSearchButton().assertIsNotEnabled()
        onBreadthFirstButton().assertIsNotEnabled()
        onRestoreBoardButton().assertIsNotEnabled()
        onRemoveObstaclesButton().assertIsNotEnabled()
    }

    @Test
    fun whenSearchIsInProgress_ThenQueuedNodesArePresent() = runComposeUiTest {
        setSmallBoardScreenAsContent()
        val board = onNodeWithTag(TEST_TAG_BOARD)

        onStartSearchButton().performClick()
        mainClock.advanceTimeBy(1_000)

        board.onChildren().assertAny(isQueued())
    }

    @Test
    fun whenSearchIsInProgress_ThenVisitedNodesArePresent() = runComposeUiTest {
        setSmallBoardScreenAsContent()
        val board = onNodeWithTag(TEST_TAG_BOARD)

        onStartSearchButton().performClick()

        board.onChildren().assertAny(isVisited())
    }

    @Test
    fun whenSearchIsFinished_ThenOnlyRestoreBoardButtonIsEnabled() = runComposeUiTest {
        setSmallBoardScreenAsContent()

        onStartSearchButton().performClick()
        waitUntilNodeCount(hasText(restoreBoardString) and isEnabled(), count = 1)

        onStartSearchButton().assertIsNotEnabled()
        onBreadthFirstButton().assertIsNotEnabled()
        onRestoreBoardButton().assertIsEnabled()
        onRemoveObstaclesButton().assertIsNotEnabled()
    }

    @Test
    fun whenSearchIsFinished_GivenPathExists_ThenPathIsMarkedOnTheBoard() = runComposeUiTest {
        setSmallBoardScreenAsContent()
        val board = onNodeWithTag(TEST_TAG_BOARD)

        onStartSearchButton().performClick()
        waitUntilNodeCount(isPath(), count = 7)

        board.onChildren().apply {
            repeat(4) { get(it + 1).assert(isPath()) }
            repeat(3) {
                get(getBoardSizeInNodes(board) * (it + 1) - 1).assert(isPath())
            }
        }
    }

    @Test
    fun whenSearchIsFinished_GivenPathDoesNotExist_ThenNoPathIsMarkedOnTheBoard() = runComposeUiTest {
        setSmallBoardScreenAsContent()
        val board = onNodeWithTag(TEST_TAG_BOARD)
        board.performTouchInput(this) {
            downOnNodeFromBottomRight(bottomOffsetInNodes = 1, rightOffsetInNodes = 0)
            up()
            downOnNodeFromBottomRight(bottomOffsetInNodes = 0, rightOffsetInNodes = 1)
            up()
        }

        onStartSearchButton().performClick()
        waitUntilDoesNotExist(isPath())

        board.onChildren().filter(isPath()).assertCountEquals(0)
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
private fun ComposeUiTest.setSmallBoardScreenAsContent() = setContent {
    BoardScreen(
        WindowSizeClass.calculateFromSize(
            size = DpSize(0.dp, 0.dp),
            supportedWidthSizeClasses = setOf(WindowWidthSizeClass.Compact),
            supportedHeightSizeClasses = setOf(WindowHeightSizeClass.Expanded)
        ),
        Modifier.size(400.dp)
    )
}

private fun ComposeUiTest.onStartSearchButton() = onNodeWithText(getString(Res.string.start_search))

private fun ComposeUiTest.onBreadthFirstButton() = onNodeWithText(breadthFirstString)

private fun ComposeUiTest.onDepthFirstButton() = onNodeWithText(getString(Res.string.depth_first))

private fun ComposeUiTest.onRestoreBoardButton() = onNodeWithText(restoreBoardString)

private fun ComposeUiTest.onRemoveObstaclesButton() = onNodeWithText(getString(Res.string.remove_obstacles))