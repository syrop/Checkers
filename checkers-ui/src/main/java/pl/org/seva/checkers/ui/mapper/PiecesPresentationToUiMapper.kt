package pl.org.seva.checkers.ui.mapper

import pl.org.seva.checkers.presentation.model.PiecesPresentationModel
import pl.org.seva.checkers.ui.model.PiecesUiModel

class PiecesPresentationToUiMapper {

    fun toUi(input: PiecesPresentationModel) = PiecesUiModel(
        input.whiteMen,
        input.blackMen,
        input.whiteKings,
        input.blackKings,
    )

}
