package pl.org.seva.checkers.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import pl.org.seva.checkers.domain.BlackMoveUseCase
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.presentation.architecture.BaseViewModel
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.presentation.model.PiecesPresentationModel
import pl.org.seva.checkers.presentation.model.PiecesViewState
import pl.org.seva.checkers.presentation.viewmodel.usecase.UseCaseExecutorProvider
import javax.inject.Inject

@HiltViewModel
class PiecesViewModel @Inject constructor(
    private val piecesDomainToPresentationMapper: PiecesDomainToPresentationMapper,
    private val piecesPresentationToDomainMapper: PiecesPresentationToDomainMapper,
    private val blackMoveUseCase: BlackMoveUseCase,
    useCaseExecutorProvider: UseCaseExecutorProvider,
) : BaseViewModel<PiecesViewState, Unit>(
    useCaseExecutorProvider,
) {
    override fun initialState() = PiecesViewState()

    private fun blackMove(pieces: PiecesPresentationModel) {
        execute(
            blackMoveUseCase,
            piecesPresentationToDomainMapper.toDomain(pieces),
            ::presentPieces
        )
    }

    private fun presentPieces(pieces: PiecesDomainModel) {
        val dishDetails = piecesDomainToPresentationMapper
            .toPresentation(pieces)
        updateViewState { dishDetailsReady(dishDetails) }
    }
}
