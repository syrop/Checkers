package pl.org.seva.checkers.data.datasource

import pl.org.seva.checkers.data.model.PiecesDataModel

interface PiecesDatasource {

    fun load(): Iterable<PiecesDataModel>

}
