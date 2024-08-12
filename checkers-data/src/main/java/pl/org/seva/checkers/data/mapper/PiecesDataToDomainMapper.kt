package pl.org.seva.checkers.data.mapper

import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.domain.model.PiecesDomainModel

class PiecesDataToDomainMapper {

    fun toDomain(input: PiecesDataModel) = PiecesDomainModel(
        input.id,
        input.parent,
        input.whiteMen,
        input.blackMen,
        input.whiteKings,
        input.blackKings,
        input.level,
        input.heuristics,
    )

}
