package pl.org.seva.checkers.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.org.seva.checkers.main.extension.copy

class GameVM : ViewModel() {

    private val whites = mutableListOf(*WHITE_START_POSITION.toTypedArray())

    private val blacks = mutableListOf(*BLACK_START_POSITION.toTypedArray())

    private val _gameStateFlow = MutableStateFlow(GameState(whites.copy(), blacks.copy()))
    val gameStateFlow: StateFlow<GameState> = _gameStateFlow

    fun removeWhite(x: Int, y: Int): Boolean {
        val removed = whites.remove(x to y)
        if (removed) {
            _gameStateFlow.value = GameState(whites.copy(), blacks.copy())
        }
        return removed
    }

    fun addWhite(x: Int, y: Int) {
        whites.add(x to y)
        _gameStateFlow.value = GameState(whites.copy(), blacks.copy())
    }

    fun moveTo(x: Int, y: Int) {
        _gameStateFlow.value = GameState(whites.copy(), blacks.copy(), x to y)
    }

    companion object {
        val WHITE_START_POSITION = listOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        val BLACK_START_POSITION = listOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
    }
}
