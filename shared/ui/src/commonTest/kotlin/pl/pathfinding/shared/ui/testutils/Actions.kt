package pl.pathfinding.shared.ui.testutils

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.performTouchInput

@OptIn(ExperimentalTestApi::class)
fun SemanticsNodeInteraction.performTouchInput(
    composeUiTest: ComposeUiTest,
    block: BoardTouchInjectionScope.() -> Unit
) = performTouchInput {
    block(
        BoardTouchInjectionScope(nodeSize = composeUiTest.nodeSize, delegate = this)
    )
}