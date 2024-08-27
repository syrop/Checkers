package pl.org.seva.checkers.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.org.seva.checkers.presentation.GamePresentation
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

    var sizeX = 0
    var sizeY = 0

    fun removeBlack(pair: Pair<Int, Int>) = presentation.removeBlack(pair)

    fun removeWhite(pair: Pair<Int, Int>) = presentation.removeWhite(pair.first, pair.second)

    fun storeState() = presentation.storeState()

    fun restoreState() = presentation.restoreState()

    fun addWhite(x: Int, y: Int, king: Boolean) = presentation.addWhite(x, y, king, viewModelScope)

    fun setWhiteWon() = presentation.setWhiteWon()

    fun fetchPieces() = presentation.fetchPieces(viewModelScope)

    fun reset() = presentation.reset(viewModelScope)
}
