package com.example.appdesktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import ui.components.Board
import ui.components.BoardState

fun main() = singleWindowApplication(
    title = "Pathfinding",
    state = WindowState(width = 1200.dp, height = 900.dp, position = WindowPosition.Aligned(Alignment.Center))
) {
    App()
}

@Composable
private fun App() {
    MaterialTheme(colors = darkColors()) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Board(
                BoardState(
                    startPosition = BoardState.Position(x = 0, y = 0),
                    endPosition = BoardState.Position(x = 19, y = 19),
                    sizeX = 20,
                    sizeY = 20
                )
            )
        }
    }
}