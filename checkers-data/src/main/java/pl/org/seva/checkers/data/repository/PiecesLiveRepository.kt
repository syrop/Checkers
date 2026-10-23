package pl.org.seva.checkers.data.repository

import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.data.mapper.PiecesDataToDomainMapper
import pl.org.seva.checkers.data.mapper.PiecesDomainToDataMapper
import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

class PiecesLiveRepository(
    private val piecesDatasource: PiecesDatasource,
    private val piecesDataToDomainMapper: PiecesDataToDomainMapper,
    private val piecesDomainToDataMapper: PiecesDomainToDataMapper,
) : PiecesRepository {

    private val piecesStore = mutableMapOf<String, PiecesDataModel>()

    private val leaves = mutableSetOf<String>()

    @get:Synchronized
    override var root = ""
        private set

    init {
        reset()
    }

    @Synchronized
    override operator fun get(piecesId: String) =
        piecesDataToDomainMapper.toDomain(requireNotNull(piecesStore[piecesId]) { "wrong Id: $piecesId" })

    @Synchronized
    override operator fun set(piecesId: String, value: PiecesDomainModel) {
        val currentLevel = piecesStore[piecesId]?.level ?: value.level
        piecesStore[piecesId] = piecesDomainToDataMapper.toData(value).apply {
            level = currentLevel
        }
    }

    @Synchronized
    override fun getLeaves(level: Int): Iterable<String> {
        if (level < 0) return leaves.toSet()
        return leaves.filter {
            piecesStore[it]?.level == level
        }
    }

    @Synchronized
    override fun find(sought: PiecesDomainModel): String {
        val soughtWhiteMen = sought.whiteMen.toSet()
        val soughtBlackMen = sought.blackMen.toSet()
        val soughtWhiteKings = sought.whiteKings.toSet()
        val soughtBlackKings = sought.blackKings.toSet()
        val filtered = piecesStore.filter {
            it.value.level == 1 &&
                    it.value.whiteMen.toSet() == soughtWhiteMen &&
                    it.value.blackMen.toSet() == soughtBlackMen &&
                    it.value.whiteKings.toSet() == soughtWhiteKings &&
                    it.value.blackKings.toSet() == soughtBlackKings
        }
        return if (filtered.isEmpty()) "" else filtered.keys.first()
    }

    @Synchronized
    override fun prune(id: String) {
        if (!piecesStore.containsKey(id)) return
        val targetLevel = piecesStore.getValue(id).level
        val toDelete = piecesStore.values.filter {
            var item = it
            val visited = mutableSetOf<String>()
            while (visited.add(item.id)) {
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
        val updatedEntries = piecesStore.mapValues { (key, value) ->
            value.copy(
                parent = if (key == id) "" else value.parent,
                heuristics = null,
            ).apply {
                level = value.level - targetLevel
            }
        }
        piecesStore.clear()
        piecesStore.putAll(updatedEntries)
        root = id
    }

    @Synchronized
    override fun updateState(state: PiecesDomainModel) {
        root = state.id
        leaves.clear()
        piecesStore.clear()
        piecesStore[root] = piecesDomainToDataMapper.toData(state).apply {
            level = 0
        }
        leaves.add(root)
    }

    @Synchronized
    override fun addLeaf(state: PiecesDomainModel) {
        piecesStore[state.id] = piecesDomainToDataMapper.toData(state).apply {
            level = state.level
        }
        leaves.remove(state.parent)
        leaves.add(state.id)
    }

    @Synchronized
    override fun reset() {
        piecesStore.clear()
        leaves.clear()
        root = ""
        piecesDatasource.load().forEach {
            piecesStore[it.id] = it.copy().apply { level = 0 }
        }
        leaves.addAll(piecesStore.values.map { it.id })
        piecesStore.forEach {
            if (it.value.parent.isEmpty()) {
                root = it.key
            } else {
                leaves.remove(it.value.parent)
                var item = it.value
                val visited = mutableSetOf(item.id)
                while (item.parent.isNotEmpty() && visited.add(item.parent)) {
                    item = requireNotNull(piecesStore[item.parent]) { "Invalid parent" }
                    it.value.level++
                }
            }
        }
    }

    @Synchronized
    override fun getImmediateMoves(): Iterable<PiecesDomainModel> {
        return piecesStore.values.filter { it.level == 1 }.map { piecesDataToDomainMapper.toDomain(it) }
    }

}
