package pl.pathfinding.shared.ui.component.board

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.mapSaver
import pl.pathfinding.pathfindingcommon.Board
import pl.pathfinding.shared.ui.component.board.pathfinder.ObservableBoard
import kotlin.reflect.KClass

private const val KEY_CLASS_NAME = "className"
private const val KEY_BOARD = "board"

fun Board.Companion.save(saverScope: SaverScope, boardToSave: Board): Any? = mapSaver<Board>(
    save = { board ->
        mapOf(
            KEY_CLASS_NAME to board::class.qualifiedName,
            KEY_BOARD to createBoardSaverFor(board::class).run { save(board) }
        )
    },
    restore = { error("Saver not intended for restoration") }
).run {
    saverScope.save(boardToSave)
}

fun Board.Companion.restore(data: Any): Board = mapSaver(
    save = { error("Saver not intended for saving") },
    restore = {
        createBoardSaverFor(
            Class.forName(it[KEY_CLASS_NAME] as String).kotlin
        ).restore(checkNotNull(it[KEY_BOARD]))
    }
).restore(data).let(::checkNotNull)


@Suppress("UNCHECKED_CAST")
private fun createBoardSaverFor(boardClass: KClass<*>): Saver<Board, Any> {
    return when (boardClass) {
        ObservableBoard::class -> ObservableBoard.Saver()
        else -> error("Unknown board class: $boardClass")
    } as Saver<Board, Any>
}
