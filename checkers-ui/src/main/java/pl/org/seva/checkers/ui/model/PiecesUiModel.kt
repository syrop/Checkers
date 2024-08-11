package pl.org.seva.checkers.ui.model

data class PiecesUiModel(
    val whiteMen: Iterable<Pair<Int, Int>>,
    val blackMen: Iterable<Pair<Int, Int>>,
    val whiteKings: Iterable<Pair<Int, Int>>,
    val blackKings: Iterable<Pair<Int, Int>>,
    val movingWhiteMan: Pair<Int, Int> = -1 to -1,
    val movingWhiteKing: Pair<Int, Int> = -1 to -1,
)
