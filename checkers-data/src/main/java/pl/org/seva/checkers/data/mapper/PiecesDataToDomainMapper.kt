package pl.org.seva.checkers.data.mapper

import pl.org.seva.checkers.data.model.PiecesResponseDataModel
import pl.org.seva.checkers.domain.model.PiecesDomainModel

class PiecesDataToDomainMapper {

    fun toDomain(input: PiecesResponseDataModel) = PiecesDomainModel(
        input.id,
        input.parent,
        input.whiteMen,
        input.blackMen,
        input.whiteKings,
        input.blackKings,
        input.level,
    )

}
