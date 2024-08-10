package pl.org.seva.checkers.domain.game.model

data class PiecesDomainModel(
    val whiteMen: List<Pair<Int, Int>>,
    val blackMen: List<Pair<Int, Int>>,
    val whiteKings: List<Pair<Int, Int>>,
    val blackKings: List<Pair<Int, Int>>,
    val level: Int
)
