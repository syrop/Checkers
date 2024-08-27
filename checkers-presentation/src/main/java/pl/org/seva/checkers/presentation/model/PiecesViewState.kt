package pl.org.seva.checkers.presentation.model

data class PiecesViewState(

    val isLoading: Boolean = true,
    val pieces: PiecesPresentationModel = PiecesPresentationModel(
        emptySet(),
        emptySet(),
        emptySet(),
        emptySet(),
    ),
    val movingWhiteMan: Pair<Int, Int> = -1 to -1,
    val movingWhiteKing: Pair<Int, Int> = -1 to -1,
    val whiteWon: Boolean = false,
    val blackWon: Boolean = false,
) {

    fun containsWhiteKing(pair: Pair<Int, Int>) = pieces.whiteKings.contains(pair)

    fun containsWhite(pair: Pair<Int, Int>) = pieces.whiteMen.contains(pair) || containsWhiteKing(pair)

    fun containsBlack(pair: Pair<Int, Int>) = pieces.blackMen.contains(pair) || pieces.blackKings.contains(pair)

    fun loading() = copy(isLoading = true)

    fun withPieces(
        pieces: PiecesPresentationModel,
    ) = copy(pieces = pieces, isLoading = false)

    fun stopMovement() = copy(movingWhiteMan = -1 to -1, movingWhiteKing = -1 to -1)

    fun moveMan(pair: Pair<Int, Int>) = copy(movingWhiteMan = pair)

    fun moveKing(pair: Pair<Int, Int>) = copy(movingWhiteKing = pair)

    fun addWhiteMan(pair: Pair<Int, Int>) = withPieces(pieces.copy(whiteMen = pieces.whiteMen + pair))

    fun addWhiteKing(pair: Pair<Int, Int>) = withPieces(pieces.copy(whiteKings = pieces.whiteKings + pair))

}
