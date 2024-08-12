package pl.org.seva.checkers.data.model

data class PiecesDataModel(
    val id: String,
    val parent: String,
    val whiteMen: List<Pair<Int, Int>>,
    val blackMen: List<Pair<Int, Int>>,
    val whiteKings: List<Pair<Int, Int>>,
    val blackKings: List<Pair<Int, Int>>,
    val heuristics: Int?,
) {
    var level = 0
}

