package pl.org.seva.checkers.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.usecase.BlackMoveUseCase
import pl.org.seva.checkers.domain.usecase.FetchPiecesUseCase
import pl.org.seva.checkers.domain.usecase.ResetUseCase
import pl.org.seva.checkers.domain.usecase.WhiteMoveUseCase
import pl.org.seva.checkers.presentation.architecture.BasePresentation
import pl.org.seva.checkers.presentation.architecture.UseCaseExecutorProvider
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.presentation.model.PiecesViewState

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
    private var storedState: PiecesViewState? = null
    private var activeMoveJob: Job? = null

    override val initialViewState = PiecesViewState()

    private fun blackMove(coroutineScope: CoroutineScope) {
        updateViewState(viewState.value.loading())
        lastMove = LastMove.BLACK
        activeMoveJob = execute(
            blackMoveUseCase,
            coroutineScope,
            Unit,
            onSuccess = { presentPieces(it, coroutineScope) },
            onException = { updateViewState { copy(isLoading = false) } },
        )
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
        execute(
            fetchPiecesUseCase,
            coroutineScope,
            Unit,
            onSuccess = { presentPieces(it, coroutineScope) },
            onException = { updateViewState { copy(isLoading = false) } },
        )
    }

    fun reset(coroutineScope: CoroutineScope) {
        activeMoveJob?.cancel()
        activeMoveJob = null
        lastMove = LastMove.NONE
        updateViewState(initialViewState)
        execute(
            resetUseCase,
            coroutineScope,
            Unit,
            onSuccess = { presentPieces(it, coroutineScope) },
            onException = { updateViewState { copy(isLoading = false) } },
        )
    }

    fun storeState() {
        storedState = viewState.value
    }

    fun restoreState() {
        storedState?.let { updateViewState(it) }
    }

    fun addWhite(x: Int, y: Int, forceKing: Boolean, coroutineScope: CoroutineScope) {
        updateViewState(
            if (forceKing || y == 0) {
                viewState.value.addWhiteKing(x to y)
            } else {
                viewState.value.addWhiteMan(x to y)
            }
        )
        lastMove = LastMove.WHITE
        activeMoveJob?.cancel()
        activeMoveJob = execute(
            whiteMoveUseCase,
            coroutineScope,
            piecesPresentationToDomainMapper.toDomain(viewState.value.pieces),
            onSuccess = { presentPieces(it, coroutineScope) },
            onException = { updateViewState { copy(isLoading = false) } },
        )
    }

    private fun whiteWon() = viewState.value.pieces.blackMen.none() &&
            viewState.value.pieces.blackKings.none()

    private fun blackWon() = viewState.value.pieces.whiteMen.none() &&
            viewState.value.pieces.whiteKings.none()

    private fun presentPieces(pieces: PiecesDomainModel, coroutineScope: CoroutineScope) {
        updateViewState { withPieces(piecesDomainToPresentationMapper.toPresentation(pieces)) }
        if (lastMove == LastMove.WHITE) {
            if (whiteWon()) {
                updateViewState { copy(whiteWon = true) }
            } else {
                updateViewState(viewState.value.loading())
                blackMove(coroutineScope)
            }
        } else if (lastMove == LastMove.BLACK && blackWon()) {
            updateViewState { copy(blackWon = true) }
        }
    }

}
