package pl.org.seva.checkers.datasource

import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.data.model.PiecesResponseDataModel
import pl.org.seva.checkers.datasource.mapper.PiecesMemoryToDataMapper
import pl.org.seva.checkers.datasource.model.PiecesMemoryModel

class PiecesLiveDatasource(
    private val piecesStore: Iterable<PiecesMemoryModel>,
    private val piecesMemoryToDataMapper: PiecesMemoryToDataMapper,
) : PiecesDatasource {

    override fun load(): Iterable<PiecesResponseDataModel> {
        return piecesStore.map { piecesMemoryToDataMapper.toData(it) }
    }
}
