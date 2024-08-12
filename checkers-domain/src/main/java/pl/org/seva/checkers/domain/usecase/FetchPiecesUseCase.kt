package pl.org.seva.checkers.domain.usecase

import pl.org.seva.checkers.domain.cleanarchitecture.usecase.BackgroundExecutingUseCase
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

class FetchPiecesUseCase(
    private val piecesRepository: PiecesRepository,
) : BackgroundExecutingUseCase<Unit, PiecesDomainModel>() {

    override suspend fun executeInBackground(request: Unit) =
        piecesRepository[piecesRepository.root]

}
