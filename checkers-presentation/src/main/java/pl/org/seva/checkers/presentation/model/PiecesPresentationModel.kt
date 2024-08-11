package pl.org.seva.checkers.presentation.model

data class PiecesPresentationModel(
    val whiteMen: Iterable<Pair<Int, Int>>,
    val blackMen: Iterable<Pair<Int, Int>>,
    val whiteKings: Iterable<Pair<Int, Int>>,
    val blackKings: Iterable<Pair<Int, Int>>,
) {

    fun isEmpty(pair: Pair<Int, Int>) = !containsWhite(pair) && !containsBlack(pair)

    private fun containsWhite(pair: Pair<Int, Int>) = whiteMen.contains(pair) || whiteKings.contains(pair)

    private fun containsBlack(pair: Pair<Int, Int>) = blackMen.contains(pair) || blackKings.contains(pair)

    fun removeWhite(pair: Pair<Int, Int>) = copy(
        blackMen = whiteMen.filter { it != pair },
        blackKings = whiteKings.filter { it != pair },
    )

    fun removeBlack(pair: Pair<Int, Int>) = copy(
        blackMen = blackMen.filter { it != pair },
        blackKings = blackKings.filter { it != pair },
    )
}
