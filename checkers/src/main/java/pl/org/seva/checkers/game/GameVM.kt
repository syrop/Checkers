package pl.org.seva.checkers.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameVM : ViewModel() {

    private val _dataFlow = MutableStateFlow(GameData(whiteStartPosition, blackStartPosition))
    val dataFlow: StateFlow<GameData> = _dataFlow

    val gameData = GameData(whiteStartPosition, blackStartPosition)

    companion object {
        val whiteStartPosition = arrayListOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        val blackStartPosition = arrayListOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
    }
}
