package pl.org.seva.checkers.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import pl.org.seva.checkers.domain.usecase.BlackMoveUseCase
import pl.org.seva.checkers.domain.usecase.FetchPiecesUseCase
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.usecase.ResetUseCase
import pl.org.seva.checkers.domain.usecase.WhiteMoveUseCase
import pl.org.seva.checkers.presentation.architecture.BasePresentation
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.presentation.model.PiecesViewState
import pl.org.seva.checkers.presentation.architecture.UseCaseExecutorProvider

class GamePresentation(
    private val piecesDomainToPresentationMapper: PiecesDomainToPresentationMapper,
    private val piecesPresentationToDomainMapper: PiecesPresentationToDomainMapper,
    private val whiteMoveUseCase: WhiteMoveUseCase,
    private val blackMoveUseCase: BlackMoveUseCase,
    private val fetchPiecesUseCase: FetchPiecesUseCase,
    private val resetUseCase: ResetUseCase,
    useCaseExecutorProvider: UseCaseExecutorProvider,
) : BasePresentation<PiecesViewState>(
    useCaseExecutorProvider,
) {

    enum class LastMove { WHITE, BLACK, NONE }

    private var lastMove = LastMove.NONE
    var whiteWon = MutableStateFlow(false)
    var blackWon = MutableStateFlow(false)

    private lateinit var storedState: PiecesViewState

    override val initialViewState = PiecesViewState()

    private fun blackMove(coroutineScope: CoroutineScope) {
        updateViewState(viewState.value.loading())
        lastMove = LastMove.BLACK
        execute(blackMoveUseCase, coroutineScope, Unit, ::presentPieces)
    }

    fun removeWhite(x: Int, y: Int): Boolean {
        val removed = viewState.value.pieces.removeWhite(x to y)
        val result = removed != viewState.value.pieces
        updateViewState(viewState.value.withPieces(removed))
        return result
    }

    fun removeBlack(pair: Pair<Int, Int>): Boolean {
        val removed = viewState.value.pieces.removeBlack(pair)
        val result = viewState.value.pieces != removed
        updateViewState(viewState.value.withPieces(removed))
        return result
    }

    fun fetchPieces(coroutineScope: CoroutineScope) {
        execute(fetchPiecesUseCase, coroutineScope, Unit, ::presentPieces)
    }

    fun reset(coroutineScope: CoroutineScope) {
        whiteWon.value = false
        blackWon.value = false
        updateViewState(initialViewState)
        execute(resetUseCase, coroutineScope, Unit, ::presentPieces)
        fetchPieces(coroutineScope)
    }

    fun storeState() {
        storedState = viewState.value
    }

    fun restoreState() {
        updateViewState(storedState)
    }

    fun setWhiteWon() {
        whiteWon.value = true
    }

    fun addWhite(x: Int, y: Int, forceKing: Boolean, coroutineScope: CoroutineScope) {
        updateViewState(if (forceKing || y == 0) {
            viewState.value.addWhiteKing(x to y)
        }
        else {
            viewState.value.addWhiteMan(x to y)
        })
        lastMove = LastMove.WHITE
        execute(whiteMoveUseCase, coroutineScope, piecesPresentationToDomainMapper.toDomain(viewState.value.pieces), ::presentPieces)
    }

    private fun whiteWon() = viewState.value.pieces.blackMen.toSet().isEmpty() &&
            viewState.value.pieces.blackKings.toSet().isEmpty()

    private fun blackWon() = viewState.value.pieces.whiteMen.toSet().isEmpty() &&
            viewState.value.pieces.whiteKings.toSet().isEmpty()

    private fun presentPieces(pieces: PiecesDomainModel, coroutineScope: CoroutineScope) {
        updateViewState { withPieces(piecesDomainToPresentationMapper.toPresentation(pieces)) }
        if (lastMove == LastMove.WHITE) {
            if (whiteWon()) {
                whiteWon.value = true
            }
            else {
                updateViewState(viewState.value.loading())
                blackMove(coroutineScope)
            }
        }
        else if (lastMove == LastMove.BLACK && blackWon()) {
            blackWon.value = true
        }
    }

}
