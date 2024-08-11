package pl.org.seva.checkers.datasource.mapper

import pl.org.seva.checkers.data.model.PiecesResponseDataModel
import pl.org.seva.checkers.datasource.model.PiecesMemoryModel

class PiecesMemoryToResponseMapper {

    fun toData(input: PiecesMemoryModel) = PiecesResponseDataModel(
        input.id,
        input.parent,
        input.whiteMen,
        input.blackMen,
        input.whiteKings,
        input.blackKings,
    )

}
