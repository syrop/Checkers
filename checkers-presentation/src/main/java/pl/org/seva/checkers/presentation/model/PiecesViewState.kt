package pl.org.seva.checkers.presentation.model

data class PiecesViewState(
    val isLoading: Boolean = true,
    val pieces: PiecesPresentationModel = PiecesPresentationModel(
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
    ),
    val whiteWon: Boolean = false,
    val blackWon: Boolean = false,
) {

    fun loading() = copy(isLoading = true)

    fun withPieces(
        pieces: PiecesPresentationModel,
    ) = copy(pieces = pieces, isLoading = false)

    fun addWhiteMan(pair: Pair<Int, Int>) = withPieces(pieces.copy(whiteMen = pieces.whiteMen + pair))

    fun addWhiteKing(pair: Pair<Int, Int>) = withPieces(pieces.copy(whiteKings = pieces.whiteKings + pair))

}
