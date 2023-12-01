package com.example.appdesktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.launch
import ui.components.board.Board
import ui.components.board.rememberBoardState

fun main() = singleWindowApplication(
    title = "Pathfinding",
    state = WindowState(width = 1200.dp, height = 900.dp, position = WindowPosition.Aligned(Alignment.Center))
) {
    App()
}

@Composable
private fun App() {
    val scope = rememberCoroutineScope()
    val boardState = rememberBoardState(sizeX = 20, sizeY = 20)

    MaterialTheme(colors = darkColors()) {
        Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Board(boardState)
            Button(onClick = { scope.launch { boardState.startSearch() } }) {
                Text("Start search")
            }
        }
    }
}