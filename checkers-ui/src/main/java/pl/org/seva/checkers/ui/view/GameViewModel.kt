package pl.org.seva.checkers.ui.view

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.org.seva.checkers.presentation.viewmodel.GamePresentation
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val presentation: GamePresentation
) : ViewModel() {

    val viewState
        get() = presentation.viewState

    val whiteWon
        get() = presentation.whiteWon

    val blackWon
        get() = presentation.blackWon

    var isWhiteMoving
        get() = presentation.isWhiteMoving
        set(value) {
            presentation.isWhiteMoving = value
        }

    var sizeX = 0
    var sizeY = 0

    fun getX(x: Float) = x.toInt() * 8 / sizeX

    fun getY(y: Float) = y.toInt() * 8 / sizeY - 1

    fun isEmpty(x: Int, y: Int) = presentation.isEmpty(x, y)

    fun removeWhite(x: Int, y: Int) = presentation.removeWhite(x, y)

    fun removeBlack(pair: Pair<Int, Int>) = presentation.removeBlack(pair)

    fun storeState() = presentation.storeState()

    fun restoreState() = presentation.restoreState()

    fun containsWhiteKing(x: Int, y: Int) = presentation.containsWhiteKing(x, y)

    fun moveWhiteManTo(x: Int, y: Int) = presentation.moveWhiteManTo(x, y)

    fun moveWhiteKingTo(x: Int, y: Int) = presentation.moveWhiteKingTo(x, y)

    fun stopMovement() = presentation.stopMovement()

    fun addWhite(x: Int, y: Int, king: Boolean) = presentation.addWhite(x, y, king, viewModelScope)

    fun setWhiteWon() = presentation.setWhiteWon()

    fun fetchPieces() = presentation.fetchPieces(viewModelScope)

    fun reset() = presentation.reset(viewModelScope)
}
