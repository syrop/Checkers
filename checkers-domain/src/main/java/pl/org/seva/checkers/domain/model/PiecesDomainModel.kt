package pl.org.seva.checkers.domain.model

data class PiecesDomainModel(
    val id: String,
    val parent: String,
    val whiteMen: List<Pair<Int, Int>>,
    val blackMen: List<Pair<Int, Int>>,
    val whiteKings: List<Pair<Int, Int>>,
    val blackKings: List<Pair<Int, Int>>,
    val level: Int = 0,
    val heuristics: Int? = null,
)
