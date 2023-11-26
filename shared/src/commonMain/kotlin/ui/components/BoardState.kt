package ui.components

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

class BoardState(val startPosition: Position, val endPosition: Position, val sizeX: Int, val sizeY: Int) {
    private val fieldSize = 20
    val fieldSizeDp = fieldSize.dp
    private val boardSize = IntSize(sizeX * fieldSize, sizeY * fieldSize)
    private var draggedField: FieldState? = null
    private val isDraggingField get() = draggedField != null
    private var toggleToFieldState: FieldState? = null
    private var previousDragFieldIndex = -1
    private val _fields = mutableStateListOf<FieldState>().apply(::fillBoard)

    private fun fillBoard(fields: SnapshotStateList<FieldState>) {
        val startPosition = startPosition.run { getFieldIndex(x, y) }
        val endPosition = endPosition.run { getFieldIndex(x, y) }
        fields.addAll(
            (0..<sizeX * sizeY).map { index ->
                when (index) {
                    startPosition -> FieldState.START
                    endPosition -> FieldState.DESTINATION
                    else -> FieldState.EMPTY
                }
            }
        )
    }

    fun onFieldClick(pointerPosition: Offset) {
        getFieldIndexFor(pointerPosition)?.let { fieldIndex ->
            val field = _fields[fieldIndex]
            if (field.isToggleable) {
                toggleField(fieldIndex)
            }
        }
    }

    fun onDragStart(pointerPosition: Offset) {
        getFieldIndexFor(pointerPosition)?.let { fieldIndex ->
            val field = _fields[fieldIndex]
            if (field.isDraggable) {
                draggedField = field
                previousDragFieldIndex = fieldIndex
            } else {
                toggleToFieldState = _fields[fieldIndex].toggleState
            }
        }
    }

    fun onDrag(pointerPosition: Offset) {
        getFieldIndexFor(pointerPosition)?.let { fieldIndex ->
            if (fieldIndex != previousDragFieldIndex) {
                if (isDraggingField) {
                    moveField(fieldIndex)
                } else {
                    toggleField(fieldIndex)
                    previousDragFieldIndex = fieldIndex
                }
            }
        }
    }

    fun onDragEnd() {
        draggedField = null
    }

    private fun getFieldIndexFor(pointerPosition: Offset): Int? {
        return if (pointerPosition.x < boardSize.width && pointerPosition.y < boardSize.height) {
            (pointerPosition.y.toInt() / fieldSize * sizeX) + (pointerPosition.x.toInt() / fieldSize)
        } else {
            null
        }
    }

    private fun moveField(destinationFieldIndex: Int) {
        val sourceFieldIndex = _fields.indexOf(draggedField)
        val destinationField = _fields[destinationFieldIndex]

        if (destinationField == FieldState.EMPTY) {
            _fields[destinationFieldIndex] = _fields[sourceFieldIndex]
            _fields[sourceFieldIndex] = FieldState.EMPTY
        }
    }

    private fun toggleField(fieldIndex: Int) {
        val field = _fields[fieldIndex]
        if (field.isToggleable) {
            _fields[fieldIndex] = checkNotNull(toggleToFieldState)
        }
    }

    private fun getFieldIndex(y: Int, x: Int) = sizeX * y + x

    fun getFieldStateAtPosition(x: Int, y: Int): FieldState {
        return _fields[getFieldIndex(y, x)]
    }

    enum class FieldState(val color: Color, val isDraggable: Boolean) {
        START(Color.Green, isDraggable = true),
        DESTINATION(Color.Red, isDraggable = true),
        EMPTY(Color.White, isDraggable = false) {
            override val toggleState get() = OBSTACLE
        },
        OBSTACLE(Color.Black, isDraggable = false) {
            override val toggleState get() = EMPTY
        },
        PATH(Color.Cyan, isDraggable = false),
        TRAVERSED(Color.Gray, isDraggable = false),
        TRAVERSE_CANDIDATE(Color.Blue, isDraggable = false);

        open val toggleState: FieldState? = null
        val isToggleable get() = toggleState != null
    }

    class Position(val x: Int, val y: Int)
}