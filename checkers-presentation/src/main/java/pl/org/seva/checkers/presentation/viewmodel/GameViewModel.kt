package pl.org.seva.checkers.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.org.seva.checkers.domain.usecase.BlackMoveUseCase
import pl.org.seva.checkers.domain.usecase.FetchPiecesUseCase
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.usecase.WhiteMoveUseCase
import pl.org.seva.checkers.presentation.architecture.BaseViewModel
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.presentation.model.PiecesViewState
import pl.org.seva.checkers.presentation.viewmodel.usecase.UseCaseExecutorProvider
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val piecesDomainToPresentationMapper: PiecesDomainToPresentationMapper,
    private val piecesPresentationToDomainMapper: PiecesPresentationToDomainMapper,
    private val whiteMoveUseCase: WhiteMoveUseCase,
    private val blackMoveUseCase: BlackMoveUseCase,
    private val fetchPiecesUseCase: FetchPiecesUseCase,
    useCaseExecutorProvider: UseCaseExecutorProvider,
) : BaseViewModel<PiecesViewState, Unit>(
    useCaseExecutorProvider,
) {

    var isWhiteMoving = true
    var whiteWon by mutableStateOf(false)
    var blackWon by mutableStateOf(false)

    private lateinit var storedState: PiecesViewState

    var sizeX = 0
    var sizeY = 0

    fun getX(x: Float) = x.toInt() * 8 / sizeX

    fun getY(y: Float) = y.toInt() * 8 / sizeY - 1


    override fun initialState() = PiecesViewState()

    fun blackMove() {
        isWhiteMoving = false
        execute(blackMoveUseCase, ::presentPieces)
    }

    fun isEmpty(x: Int, y: Int) = viewState.pieces.isEmpty(x to y)

    fun containsWhiteKing(x: Int, y: Int) = viewState.containsWhiteKing(x to y)

    fun removeWhite(x: Int, y: Int): Boolean {
        val removed = viewState.pieces.removeWhite(x to y)
        val result = removed != viewState.pieces
        updateViewState(viewState.withPieces(removed))
        return result
    }

    fun removeBlack(pair: Pair<Int, Int>): Boolean {
        val removed = viewState.pieces.removeBlack(pair)
        val result = viewState.pieces != removed
        updateViewState(viewState.withPieces(removed))
        return result
    }

    fun fetchPieces() {
        execute(fetchPiecesUseCase, ::presentPieces)
    }

    fun reset() {
        whiteWon = false
        blackWon = false
        isWhiteMoving = true
        updateViewState(initialState())
        fetchPieces()
    }

    fun storeState() {
        storedState = viewState
    }

    fun restoreState() {
        updateViewState(storedState)
    }

    fun setWhiteWon() {
        whiteWon = true
    }

    fun moveWhiteManTo(x: Int, y: Int) {
        updateViewState(viewState.moveMan(x to y))
    }

    fun moveWhiteKingTo(x: Int, y: Int) {
        updateViewState(viewState.moveKing(x to y))
    }

    fun addWhite(x: Int, y: Int, forceKing: Boolean = false) {
        updateViewState(if (forceKing || y == 0) {
            viewState.addWhiteKing(x to y)
        }
        else {
            viewState.addWhiteMan(x to y)
        })
        execute(whiteMoveUseCase, piecesPresentationToDomainMapper.toDomain(viewState.pieces))
    }

    fun stopMovement() = updateViewState(viewState.stopMovement())

    private fun presentPieces(pieces: PiecesDomainModel) {
        isWhiteMoving = true
        updateViewState { withPieces(piecesDomainToPresentationMapper.toPresentation(pieces)) }
    }

}
