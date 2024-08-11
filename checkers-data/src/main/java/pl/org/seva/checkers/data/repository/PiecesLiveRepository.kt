package pl.org.seva.checkers.data.repository

import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.data.mapper.PiecesDataToDomainMapper
import pl.org.seva.checkers.data.mapper.PiecesDomainToDataMapper
import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

class PiecesLiveRepository(
    piecesDatasource: PiecesDatasource,
    private val piecesDataToDomainMapper: PiecesDataToDomainMapper,
    private val piecesDomainToDataMapper: PiecesDomainToDataMapper,
) : PiecesRepository {

    private val piecesStore = mutableMapOf<String, PiecesDataModel>()

    private val leaves = mutableSetOf<String>()

    override var root = ""
        private set

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

    override operator fun get(piecesId: String) =
        piecesDataToDomainMapper.toDomain(requireNotNull(piecesStore[piecesId]) { "wrong Id: $piecesId" })

    override fun getLeaves(level: Int): Iterable<String> {
        if (level < 0) return leaves
        return leaves.filter { get(it).level == level }
    }

    override fun find(sought: PiecesDomainModel): String {
        val filtered = piecesStore.filter {
            it.value.whiteMen == sought.whiteMen &&
                    it.value.blackMen == sought.blackMen &&
                    it.value.whiteKings == sought.whiteKings &&
                    it.value.blackKings == sought.blackKings
        }
        return if (filtered.isEmpty()) "" else filtered.keys.first()
    }

    override fun reduce(id: String) {
        val toDelete = piecesStore.values.filter {
            var item = it
            while (true) {
                if (item.id == id) {
                    return@filter false
                }
                item = piecesStore[item.parent] ?: break
            }
            return@filter true
        }.map { it.id }
        toDelete.forEach {
            leaves.remove(it)
            piecesStore.remove(it)
        }
        piecesStore.forEach {
            it.value.level--
            if (it.value.level == 0) {
                root = it.value.id
            }
        }
    }

    override fun updateState(state: PiecesDomainModel) {
        root = state.id
        piecesStore.clear()
        piecesStore[root] = piecesDomainToDataMapper.toData(state)
        leaves.clear()
        leaves.add(root)
    }

}
