package pl.org.seva.checkers.domain.repository

import pl.org.seva.checkers.domain.model.PiecesDomainModel

interface PiecesRepository {

    val root: String

    operator fun get(piecesId: String): PiecesDomainModel

    fun getLeaves(level: Int = -1): Iterable<String>

}
