package pl.org.seva.checkers.game

data class GameState(
        val whiteMen: List<Pair<Int, Int>>,
        val blackMen: List<Pair<Int, Int>>,
        val whiteKings: List<Pair<Int, Int>>,
        val blackKings: List<Pair<Int, Int>>,
        val moving: Pair<Int, Int> = -1 to -1,
) {
    fun containsWhite(pair: Pair<Int, Int>) = whiteMen.contains(pair) || whiteKings.contains(pair)

    fun containsBlack(pair: Pair<Int, Int>) = blackMen.contains(pair) || whiteKings.contains(pair)

    fun removeWhite(pair: Pair<Int, Int>) = GameState(
            whiteMen.filter { it != pair },
            blackMen,
            whiteKings.filter { it != pair },
            blackKings,
    )

    fun removeBlack(pair: Pair<Int, Int>) = GameState(
            whiteMen,
            blackMen.filter { it != pair },
            whiteKings,
            blackKings.filter { it != pair },
    )

    fun addWhiteMan(pair: Pair<Int, Int>) = GameState(
            whiteMen + pair,
            blackMen,
            whiteKings,
            blackKings,
    )
}
