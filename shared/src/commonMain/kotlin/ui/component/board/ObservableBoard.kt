package ui.component.board

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import pathfinder.AbstractBoard

class ObservableBoard private constructor(private val sizeX: Int, private val nodes: SnapshotStateList<NodeState>) : AbstractBoard(sizeX, nodes) {

    constructor(sizeX: Int, sizeY: Int) : this(sizeX, generateNodes(sizeX, sizeY))

    override fun copy(): ObservableBoard {
        return ObservableBoard(sizeX, nodes.toMutableStateList())
    }

    companion object {
        private fun generateNodes(sizeX: Int, sizeY: Int): SnapshotStateList<NodeState> {
            val startPosition = 0
            val destinationPosition = sizeX * sizeY - 1
            return mutableStateListOf<NodeState>().apply {
                (0..<sizeX * sizeY).forEach { index ->
                    add(
                        when (index) {
                            startPosition -> NodeState.START
                            destinationPosition -> NodeState.DESTINATION
                            else -> NodeState.EMPTY
                        }
                    )
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun Saver(): Saver<ObservableBoard, Any> {
            val keySizeX = "sizeX"
            val keyNodes = "nodes"

            return mapSaver(
                save = { mapOf(keySizeX to it.sizeX, keyNodes to it.nodes.toMutableList()) },
                restore = { ObservableBoard(sizeX = it[keySizeX] as Int, nodes = (it[keyNodes] as List<NodeState>).toMutableStateList()) }
            )
        }
    }
}