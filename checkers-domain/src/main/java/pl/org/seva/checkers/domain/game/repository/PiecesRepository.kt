package pl.org.seva.checkers.domain.game.repository

import pl.org.seva.checkers.domain.game.model.PiecesDomainModel

interface PiecesRepository {
    fun getPieces(restaurantId: String): PiecesDomainModel
}
