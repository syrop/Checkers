package pl.org.seva.checkers.domain.model

import java.util.UUID
import kotlin.math.abs

data class PiecesDomainModel(
    val id: String,
    val parent: String,
    val whiteMen: List<Pair<Int, Int>>,
    val blackMen: List<Pair<Int, Int>>,
    val whiteKings: List<Pair<Int, Int>>,
    val blackKings: List<Pair<Int, Int>>,
    val level: Int = 0,
    val heuristics: Int? = null,
) {

    fun whiteWon() = blackMen.isEmpty() && blackKings.isEmpty()

    fun blackWon() = whiteMen.isEmpty() && whiteKings.isEmpty()

    fun removeWhite(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        whiteMen = whiteMen.filter { it != pair },
        whiteKings = whiteKings.filter { it != pair },
    )

    fun removeBlack(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        blackMen = blackMen.filter { it != pair },
        blackKings = blackKings.filter { it != pair },
    )

    fun containsBlack(pair: Pair<Int, Int>) =
        blackMen.contains(pair) || blackKings.contains(pair)

    fun containsWhite(pair: Pair<Int, Int>) =
        whiteMen.contains(pair) || whiteKings.contains(pair)

    fun isEmpty(pair: Pair<Int, Int>) =
        !containsWhite(pair) && !containsBlack(pair)

    fun addWhiteMan(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        whiteMen = whiteMen + pair,
    )

    fun addBlackMan(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        blackMen = blackMen + pair,
    )

    fun addWhiteKing(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        whiteKings = whiteKings + pair,
    )

    fun addBlackKing(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        blackKings = blackKings + pair,
    )

    fun isValidWhiteMove(from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
        val (x1, y1) = from
        val (x2, y2) = to
        if (from == to || x2 !in 0..7 || y2 !in 0..7 || !isEmpty(to)) return false
        val dx = x2 - x1
        val dy = y2 - y1
        if (abs(dx) != abs(dy)) return false

        return when {
            whiteKings.contains(from) -> isValidWhiteKingMove(x1, y1, x2, y2)
            whiteMen.contains(from) -> {
                (abs(dx) == 1 && dy == -1) ||
                    (abs(dx) == 2 && dy == -2 && containsBlack(predecessor(x1, y1, x2, y2)))
            }
            else -> false
        }
    }

    private fun isValidWhiteKingMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        val dirX = if (x2 > x1) 1 else -1
        val dirY = if (y2 > y1) 1 else -1
        var x = x1 + dirX
        var y = y1 + dirY
        while (x != x2 - dirX || y != y2 - dirY) {
            if (!isEmpty(x to y)) return false
            x += dirX
            y += dirY
        }
        return !containsWhite(x to y)
    }

    companion object {
        fun predecessor(x1: Int, y1: Int, x2: Int, y2: Int): Pair<Int, Int> {
            if (abs(x2 - x1) != abs(y2 - y1) || abs(x2 - x1) < 2) return -1 to -1
            val dirX = if (x2 > x1) 1 else -1
            val dirY = if (y2 > y1) 1 else -1
            return (x2 - dirX) to (y2 - dirY)
        }
    }
}
