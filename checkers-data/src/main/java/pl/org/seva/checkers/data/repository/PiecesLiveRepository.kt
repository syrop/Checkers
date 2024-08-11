package pl.org.seva.checkers.data.repository

import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.data.mapper.PiecesDataToDomainMapper
import pl.org.seva.checkers.data.model.PiecesResponseDataModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

class PiecesLiveRepository(
    piecesDatasource: PiecesDatasource,
    private val piecesDataToDomainMapper: PiecesDataToDomainMapper,
) : PiecesRepository {

    private val piecesStore = mutableMapOf<String, PiecesResponseDataModel>()

    private val leaves = mutableSetOf<String>()

    init {
        piecesDatasource.load().forEach {
            piecesStore[it.id] = it
        }
        leaves.addAll(piecesStore.values.map { it.id })
        piecesStore.forEach {
            if (it.value.parent.isEmpty()) {
                root = it.key
            }
            else {
                leaves.remove(it.value.parent)
                var item = it.value
                while (item.parent.isNotEmpty()) {
                    item = requireNotNull(piecesStore[item.parent]) { "Invalid parent" }
                    it.value.level++
                }
            }
        }
    }

    override var root = ""
        private set

    override operator fun get(piecesId: String) =
        piecesDataToDomainMapper.toDomain(requireNotNull(piecesStore[piecesId]) { "wrong Id" })

    override fun getLeaves(level: Int): Iterable<String> {
        if (level < 0) return leaves
        return leaves.filter { get(it).level == level }
    }
}
