package pl.org.seva.checkers.domain.usecase

import pl.org.seva.checkers.domain.cleanarchitecture.usecase.BackgroundExecutingUseCase
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

class WhiteMoveUseCase(
    private val piecesRepository: PiecesRepository,
) : BackgroundExecutingUseCase<PiecesDomainModel, PiecesDomainModel>() {

    override suspend fun executeInBackground(request: PiecesDomainModel): PiecesDomainModel {
        val found = piecesRepository.find(request)
        if (found.isNotEmpty()) {
            piecesRepository.prune(found)
        } else {
            piecesRepository.updateState(request)
        }
        return request
    }

}
