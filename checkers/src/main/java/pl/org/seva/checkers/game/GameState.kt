package pl.org.seva.checkers.game

data class GameState(
        val whiteMen: List<Pair<Int, Int>>,
        val blackMen: List<Pair<Int, Int>>,
        val moving: Pair<Int, Int> = -1 to -1
)
