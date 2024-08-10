package pl.org.seva.checkers.ui.model

data class PiecesUiModel(
    val whiteMen: Set<Pair<Int, Int>>,
    val blackMen: Set<Pair<Int, Int>>,
    val whiteKings: Set<Pair<Int, Int>>,
    val blackKings: Set<Pair<Int, Int>>,
    val movingWhiteMan: Pair<Int, Int> = -1 to -1,
    val movingWhiteKing: Pair<Int, Int> = -1 to -1,
)
