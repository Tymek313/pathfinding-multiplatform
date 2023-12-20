package ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pathfinding.PathfinderType
import ui.component.board.Board
import ui.component.board.BoardState
import ui.component.board.rememberBoardState
import ui.resource.StringRes

@Composable
fun BoardScreen(windowSizeClass: WindowSizeClass) {
    val boardState = rememberBoardState(sizeX = 20, sizeY = 20)
    val isWideScreen = remember(windowSizeClass) {
        windowSizeClass.widthSizeClass.let { it == WindowWidthSizeClass.Expanded || it == WindowWidthSizeClass.Medium }
    }

    if (isWideScreen) {
        WideBoardScreen(boardState)
    } else {
        NarrowBoardScreen(boardState)
    }
}

@Composable
private fun WideBoardScreen(boardState: BoardState) {
    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.fillMaxHeight().weight(1f), contentAlignment = Alignment.Center) {
            Board(state = boardState)
        }
        Card(modifier = Modifier.width(IntrinsicSize.Min).padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val controlButtonModifier = Modifier.fillMaxWidth()

                ControlButton(
                    modifier = controlButtonModifier,
                    text = StringRes.startSearch,
                    isEnabled = boardState.isBoardIdle,
                    onClick = boardState::startSearch
                )
                OutlinedControlButton(
                    modifier = controlButtonModifier,
                    text = StringRes.removeObstacles,
                    isEnabled = boardState.isBoardIdle,
                    onClick = boardState::removeObstacles
                )
                OutlinedControlButton(
                    modifier = controlButtonModifier,
                    text = StringRes.restoreBoard,
                    isEnabled = boardState.isBoardSearchFinished,
                    onClick = boardState::restoreBoard
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                PathfinderTypeDropdown(
                    isEnabled = boardState.isBoardIdle,
                    selectedPathfinderType = boardState.pathfinderType,
                    onPathfinderTypeChange = boardState::pathfinderType::set
                )
            }
        }
    }
}

@Composable
private fun NarrowBoardScreen(boardState: BoardState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Board(boardState)
        }
        Card(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PathfinderTypeDropdown(
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = boardState.isBoardIdle,
                    selectedPathfinderType = boardState.pathfinderType,
                    onPathfinderTypeChange = boardState::pathfinderType::set
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedControlButton(
                        modifier = Modifier.weight(1f),
                        text = StringRes.removeObstacles,
                        isEnabled = boardState.isBoardIdle,
                        onClick = boardState::removeObstacles
                    )
                    OutlinedControlButton(
                        modifier = Modifier.weight(1f),
                        text = StringRes.restoreBoard,
                        isEnabled = boardState.isBoardSearchFinished,
                        onClick = boardState::restoreBoard
                    )
                }
                ControlButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = StringRes.startSearch,
                    isEnabled = boardState.isBoardIdle,
                    onClick = boardState::startSearch
                )
            }
        }
    }
}

@Composable
private fun ControlButton(text: String, isEnabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Button(modifier = modifier, enabled = isEnabled, onClick = onClick) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Composable
private fun OutlinedControlButton(text: String, isEnabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    OutlinedButton(modifier = modifier, enabled = isEnabled, onClick = onClick) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PathfinderTypeDropdown(
    isEnabled: Boolean,
    selectedPathfinderType: PathfinderType,
    onPathfinderTypeChange: (PathfinderType) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPathfinderDropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isPathfinderDropdownExpanded,
        onExpandedChange = { if (isEnabled) isPathfinderDropdownExpanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            value = selectedPathfinderType.pathfinderName,
            onValueChange = {},
            enabled = isEnabled,
            label = { Text(text = StringRes.pathfindingAlgorithm) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isPathfinderDropdownExpanded) }
        )
        ExposedDropdownMenu(expanded = isPathfinderDropdownExpanded, onDismissRequest = { isPathfinderDropdownExpanded = false }) {
            PathfinderType.entries.forEach { pathfinderType ->
                DropdownMenuItem(text = { Text(pathfinderType.pathfinderName) }, onClick = { onPathfinderTypeChange(pathfinderType) })
            }
        }
    }
}

private val PathfinderType.pathfinderName
    @Composable get() = when (this) {
        PathfinderType.BREADTH_FIRST -> StringRes.breadthFirst
    }