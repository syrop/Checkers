package pl.org.seva.checkers.presentation.model

data class PiecesPresentationModel(
    val whiteMen: Iterable<Pair<Int, Int>>,
    val blackMen: Iterable<Pair<Int, Int>>,
    val whiteKings: Iterable<Pair<Int, Int>>,
    val blackKings: Iterable<Pair<Int, Int>>,
)
