package pl.pathfinding.shared.ui.testutils

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.unit.width
import kotlin.math.roundToInt

@OptIn(ExperimentalTestApi::class)
val ComposeUiTest.nodeSize
    get() = density.run { onNode(isStart()).getBoundsInRoot().width.toPx() }

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.getBoardSizeInNodes(interaction: SemanticsNodeInteraction) =
    (interaction.fetchSemanticsNode().layoutInfo.width / nodeSize).roundToInt()

