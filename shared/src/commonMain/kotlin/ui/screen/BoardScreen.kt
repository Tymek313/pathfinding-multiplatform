package ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pathfinder.PathfinderType
import ui.component.board.Board
import ui.component.board.BoardState
import ui.component.board.rememberBoardState
import ui.resource.StringRes

@Composable
fun BoardScreen(windowSizeClass: WindowSizeClass) {
    val boardState = rememberBoardState(sizeX = 20, sizeY = 20)
    val isWideScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    if (isWideScreen) {
        WideBoardScreen(boardState)
    } else {
        NarrowBoardScreen(boardState)
    }
}

@Composable
private fun WideBoardScreen(boardState: BoardState) {
    Row {
        Board(boardState)
        Column {
            BoardControls(isWideScreen = true, boardState)
        }
    }
}

@Composable
private fun NarrowBoardScreen(boardState: BoardState) {
    Column {
        Board(boardState)
        BoardControls(isWideScreen = false, boardState)
    }
}

@Composable
private fun BoardControls(isWideScreen: Boolean, boardState: BoardState) {
    val canChangePathfinder by remember { derivedStateOf { !boardState.isInteractionLocked } }
    val controlButtonModifier = if (isWideScreen) Modifier else Modifier.fillMaxWidth()

    ControlButton(
        modifier = controlButtonModifier,
        text = StringRes.startSearch,
        enabled = !boardState.isInteractionLocked,
        onClick = { boardState.startSearch() }
    )
    ControlButton(
        modifier = controlButtonModifier,
        text = StringRes.removeObstacles,
        enabled = !boardState.isInteractionLocked,
        onClick = { boardState.removeObstacles() }
    )
    ControlButton(
        modifier = controlButtonModifier,
        text = StringRes.restoreBoard,
        enabled = boardState.isSearchFinished,
        onClick = { boardState.restoreBoard() }
    )

    PathfinderTypeDropdown(
        canChangePathfinder = canChangePathfinder,
        selectedPathfinderType = boardState.pathfinderType,
        onPathfinderTypeChange = boardState::pathfinderType::set
    )
}

@Composable
private fun ControlButton(modifier: Modifier, text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(modifier = modifier, enabled = enabled, onClick = onClick) {
        Text(text)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PathfinderTypeDropdown(canChangePathfinder: Boolean, selectedPathfinderType: PathfinderType, onPathfinderTypeChange: (PathfinderType) -> Unit) {
    var isPathfinderDropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isPathfinderDropdownExpanded,
        onExpandedChange = { if (canChangePathfinder) isPathfinderDropdownExpanded = it }
    ) {
        OutlinedTextField(
            value = selectedPathfinderType.pathfinderName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(),
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