package pl.pathfinding.shared.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pathfinding.shared.ui.generated.resources.Res
import pathfinding.shared.ui.generated.resources.breadth_first
import pathfinding.shared.ui.generated.resources.depth_first
import pathfinding.shared.ui.generated.resources.pathfinding_algorithm
import pathfinding.shared.ui.generated.resources.remove_obstacles
import pathfinding.shared.ui.generated.resources.restore_board
import pathfinding.shared.ui.generated.resources.start_search
import pl.pathfinding.shared.domain.pathfinder.PathfinderType
import pl.pathfinding.shared.ui.component.board.Board
import pl.pathfinding.shared.ui.component.board.BoardState
import pl.pathfinding.shared.ui.component.board.rememberBoardState

private val screenModifier = Modifier.safeDrawingPadding()

@Composable
fun BoardScreen(windowSizeClass: WindowSizeClass, modifier: Modifier = Modifier) {
    val boardState = rememberBoardState()
    val isWideScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded ||
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    if (isWideScreen) {
        WideBoardScreen(boardState, modifier.then(screenModifier))
    } else {
        NarrowBoardScreen(boardState, modifier.then(screenModifier))
    }
}

private val controlButtonModifier = Modifier.fillMaxWidth()

@Composable
private fun WideBoardScreen(boardState: BoardState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    Row(modifier = modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.fillMaxHeight().weight(1f), contentAlignment = Alignment.Center) {
            Board(boardState)
        }
        Card(modifier = Modifier.width(IntrinsicSize.Min).padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ControlButton(
                    modifier = controlButtonModifier,
                    text = stringResource(Res.string.start_search),
                    isEnabled = boardState.isBoardIdle,
                    onClick = { scope.launch { boardState.startSearch() } }
                )
                OutlinedControlButton(
                    modifier = controlButtonModifier,
                    text = stringResource(Res.string.remove_obstacles),
                    isEnabled = boardState.isBoardIdle,
                    onClick = boardState::removeObstacles
                )
                OutlinedControlButton(
                    modifier = controlButtonModifier,
                    text = stringResource(Res.string.restore_board),
                    isEnabled = boardState.isBoardSearchFinished,
                    onClick = boardState::restoreBoard
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
private fun NarrowBoardScreen(boardState: BoardState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Board(boardState)
        }
        Card(modifier = Modifier.padding(16.dp).widthIn(max = 600.dp).align(Alignment.CenterHorizontally)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PathfinderTypeDropdown(
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = boardState.isBoardIdle,
                    selectedPathfinderType = boardState.pathfinderType,
                    onPathfinderTypeChange = boardState::pathfinderType::set
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedControlButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(Res.string.remove_obstacles),
                        isEnabled = boardState.isBoardIdle,
                        onClick = boardState::removeObstacles
                    )
                    OutlinedControlButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(Res.string.restore_board),
                        isEnabled = boardState.isBoardSearchFinished,
                        onClick = boardState::restoreBoard
                    )
                }
                ControlButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.start_search),
                    isEnabled = boardState.isBoardIdle,
                    onClick = { scope.launch { boardState.startSearch() } }
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
            modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
            value = stringResource(selectedPathfinderType.pathfinderNameRes),
            onValueChange = {},
            enabled = isEnabled,
            label = { Text(text = stringResource(Res.string.pathfinding_algorithm)) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isPathfinderDropdownExpanded) }
        )
        ExposedDropdownMenu(
            expanded = isPathfinderDropdownExpanded,
            onDismissRequest = { isPathfinderDropdownExpanded = false }
        ) {
            PathfinderType.entries.forEach { pathfinderType ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(pathfinderType.pathfinderNameRes)) },
                    onClick = {
                        onPathfinderTypeChange(pathfinderType)
                        isPathfinderDropdownExpanded = false
                    }
                )
            }
        }
    }
}

private val PathfinderType.pathfinderNameRes
    get() = when (this) {
        PathfinderType.BREADTH_FIRST -> Res.string.breadth_first
        PathfinderType.DEPTH_FIRST -> Res.string.depth_first
    }
