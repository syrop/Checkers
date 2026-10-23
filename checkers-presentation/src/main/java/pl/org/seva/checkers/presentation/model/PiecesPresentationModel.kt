package pl.org.seva.checkers.presentation.model

data class PiecesPresentationModel(
    val whiteMen: Iterable<Pair<Int, Int>>,
    val blackMen: Iterable<Pair<Int, Int>>,
    val whiteKings: Iterable<Pair<Int, Int>>,
    val blackKings: Iterable<Pair<Int, Int>>,
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
