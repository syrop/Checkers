package pl.org.seva.checkers.data.mapper

import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.domain.model.PiecesDomainModel

class PiecesDomainToDataMapper {

    fun toData(input: PiecesDomainModel) = PiecesDataModel(
        input.id,
        input.parent,
        input.whiteMen,
        input.blackMen,
        input.whiteKings,
        input.blackKings,
        input.heuristics,
    ).apply {
        level = input.level
    }

}
