package pl.org.seva.checkers.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameVM : ViewModel() {

    var isWhiteMoving = true

    private var gameState = GameState(WHITE_START_POSITION, BLACK_START_POSITION, emptyList(), emptyList())

    private val _gameStateFlow = MutableStateFlow(gameState)
    val gameStateFlow: StateFlow<GameState> = _gameStateFlow

    fun removeWhite(x: Int, y: Int): Boolean {
        val removed = gameState.removeWhite(x to y)
        val result = removed != gameState
        _gameStateFlow.value = removed
        gameState = removed
        return result
    }

    fun addWhite(x: Int, y: Int) {
        gameState = if (y == 7) {
            gameState.addWhiteKing(x to y)
        }
        else {
            gameState.addWhiteMan(x to y)
        }
        _gameStateFlow.value = gameState
    }

    fun moveTo(x: Int, y: Int) {
        gameState = gameState.copy(movingWhiteMan = x to y)
        _gameStateFlow.value = gameState
    }

    fun removeBlack(pair: Pair<Int, Int>): Boolean {
        val removed = gameState.removeBlack(pair)
        val result = gameState == removed
        gameState = removed
        return result
    }

    fun isEmpty(x: Int, y: Int) = gameState.isEmpty(x to y)

    companion object {
        val WHITE_START_POSITION = listOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        val BLACK_START_POSITION = listOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
    }
}
