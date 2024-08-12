package pl.org.seva.checkers.presentation.mapper

import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.presentation.model.PiecesPresentationModel
import java.util.UUID

class PiecesPresentationToDomainMapper {

    fun toDomain(input: PiecesPresentationModel) = PiecesDomainModel(
        UUID.randomUUID().toString(),
        "",
        input.whiteMen.toList(),
        input.blackMen.toList(),
        input.whiteKings.toList(),
        input.blackKings.toList(),
    )

}
