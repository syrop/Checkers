package pl.org.seva.checkers.data.datasource

import pl.org.seva.checkers.data.model.PiecesResponseDataModel

interface PiecesDatasource {

    fun load(): Iterable<PiecesResponseDataModel>

}
