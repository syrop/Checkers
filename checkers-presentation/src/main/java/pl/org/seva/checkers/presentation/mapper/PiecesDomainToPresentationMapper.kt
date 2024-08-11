package pl.org.seva.checkers.presentation.mapper

import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.presentation.model.PiecesPresentationModel

class PiecesDomainToPresentationMapper {

    fun toPresentation(input: PiecesDomainModel) = PiecesPresentationModel(
        input.whiteMen,
        input.blackMen,
        input.whiteKings,
        input.blackKings,
    )

}
