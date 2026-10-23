package pl.org.seva.checkers.presentation.model

data class PiecesPresentationModel(
    val whiteMen: List<Pair<Int, Int>>,
    val blackMen: List<Pair<Int, Int>>,
    val whiteKings: List<Pair<Int, Int>>,
    val blackKings: List<Pair<Int, Int>>,
) {

    fun removeWhite(pair: Pair<Int, Int>) = copy(
        whiteMen = whiteMen.filter { it != pair },
        whiteKings = whiteKings.filter { it != pair },
    )

    fun removeBlack(pair: Pair<Int, Int>) = copy(
        blackMen = blackMen.filter { it != pair },
        blackKings = blackKings.filter { it != pair },
    )
}
