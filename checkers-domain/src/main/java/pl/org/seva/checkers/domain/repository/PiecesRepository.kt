package pl.org.seva.checkers.domain.repository

import pl.org.seva.checkers.domain.model.PiecesDomainModel

interface PiecesRepository {

    val root: String

    operator fun get(piecesId: String): PiecesDomainModel

    fun getLeaves(level: Int = -1): Iterable<String>

    fun find(sought: PiecesDomainModel): String

    fun reduce(id: String)

    fun updateState(state: PiecesDomainModel)

    fun addLeaf(state: PiecesDomainModel)

    fun reset()

}
