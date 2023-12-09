package ui.resource

import androidx.compose.runtime.Composable

interface StringResources {
    val graphPathfinding: String
    val startSearch: String
    val removeObstacles: String
    val restoreBoard: String
    val breadthFirst: String
    val pathfindingAlgorithm: String
}

internal object EnglishStringResources : StringResources {
    override val graphPathfinding = "Graph pathfinding"
    override val startSearch = "Start search"
    override val removeObstacles = "Remove obstacles"
    override val restoreBoard = "Restore board"
    override val breadthFirst = "Breadth first search"
    override val pathfindingAlgorithm = "Pathfinding algorithm"
}

internal object PolishStringResources : StringResources {
    override val graphPathfinding = "Przeszukiwanie grafu"
    override val startSearch = "Rozpocznij przeszukiwanie"
    override val removeObstacles = "Usuń przeszkody"
    override val restoreBoard = "Przywróć planszę"
    override val breadthFirst = "Przeszukiwanie w szerz"
    override val pathfindingAlgorithm = "Algorytm przeszukiwania"
}

internal expect val currentLanguage: String

val StringRes: StringResources
    @Composable get() = when (currentLanguage) {
        "pl" -> PolishStringResources
        else -> EnglishStringResources
    }