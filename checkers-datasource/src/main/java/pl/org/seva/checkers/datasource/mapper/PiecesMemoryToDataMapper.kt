package pl.org.seva.checkers.datasource.mapper

import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.datasource.model.PiecesMemoryModel

class PiecesMemoryToDataMapper {

    fun toData(input: PiecesMemoryModel) = PiecesDataModel(
        input.id,
        input.parent,
        input.whiteMen,
        input.blackMen,
        input.whiteKings,
        input.blackKings,
        null,
    )

}
