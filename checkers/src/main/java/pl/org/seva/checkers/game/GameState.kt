package pl.org.seva.checkers.game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class GameState(
        val whiteMen: List<Pair<Int, Int>>,
        val blackMen: List<Pair<Int, Int>>,
        val whiteKings: List<Pair<Int, Int>>,
        val blackKings: List<Pair<Int, Int>>,
        val movingWhiteMan: Pair<Int, Int> = -1 to -1,
        val movingWhiteKing: Pair<Int, Int> = -1 to -1,
) {

    var level = 0

    private val children = mutableListOf<GameState>()

    suspend fun populateChildren() = coroutineScope {
        fun isValidField(pair: Pair<Int, Int>) = pair.first in 0..7 && pair.second in 0..7 && isEmpty(pair)

        fun GameState.addBlack(pair: Pair<Int, Int>) = if (pair.second == 7) {
            addBlackKing(pair)
        }
        else {
            addBlackMan(pair)
        }

        fun GameState.addWhite(pair: Pair<Int, Int>) = if (pair.second == 0) {
            addWhiteKing(pair)
        }
        else {
            addWhiteMan(pair)
        }

        if (level == STEPS) {
            return@coroutineScope
        }
        if (level % 2 == 0) { // black moves
            val men = async(Dispatchers.Default) {
                blackMen
                        .map { blackMan -> // asynchronously list of moves of this one man
                            async(Dispatchers.Default) {
                                val removed = removeBlack(blackMan)
                                mutableListOf<GameState>().apply {
                                    val blackManMovesLeft = blackMan.first - 1 to blackMan.second + 1
                                    if (isValidField(blackManMovesLeft)) {
                                        add(removed.addBlack(blackManMovesLeft))
                                    }
                                    val blackManMovesRight = blackMan.first + 1 to blackMan.second + 1
                                    if (isValidField(blackManMovesRight)) {
                                        add(removed.addBlack(blackManMovesRight))
                                    }
                                }
                            }
                        }
                        .map { it.await() } // list of moves of this one man
                        .reduce { acc, list -> acc.apply { addAll(list) } } // list of moves of all black men
                        .onEach { it.level = level + 1 }
            }
            children.addAll(men.await())
        }
        else { // white moves
            val men = async(Dispatchers.Default) {
                whiteMen
                        .map { whiteMan -> // asynchronously list of moves of this one man
                            async(Dispatchers.Default) {
                                val removed = removeWhite(whiteMan)
                                mutableListOf<GameState>().apply {
                                    val whiteManMovesLeft = whiteMan.first - 1 to whiteMan.second - 1
                                    if (isValidField(whiteManMovesLeft)) {
                                        add(removed.addWhite(whiteManMovesLeft))
                                    }
                                    val whiteManMovesRight = whiteMan.first + 1 to whiteMan.second - 1
                                    if (isValidField(whiteManMovesRight)) {
                                        add(removed.addWhite(whiteManMovesRight))
                                    }
                                }
                            }
                        }
                        .map { it.await() } // list of moves of this one man
                        .reduce { acc, list -> acc.apply { addAll(list) } } // list of moves of all white men
                        .onEach { it.level = level + 1 }
            }
            children.addAll(men.await())
        }
    }

    fun isEmpty(pair: Pair<Int, Int>) = !containsWhite(pair) && !containsBlack(pair)

    fun containsWhite(pair: Pair<Int, Int>) = whiteMen.contains(pair) || whiteKings.contains(pair)

    fun containsBlack(pair: Pair<Int, Int>) = blackMen.contains(pair) || blackKings.contains(pair)

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

    fun addBlackMan(pair: Pair<Int, Int>) = GameState (
            whiteMen,
            blackMen + pair,
            whiteKings,
            blackKings,
    )

    fun addWhiteKing(pair: Pair<Int, Int>) = GameState(
            whiteMen,
            blackMen,
            whiteKings + pair,
            blackKings,
    )

    fun addBlackKing(pair: Pair<Int, Int>) = GameState(
            whiteMen,
            blackMen,
            whiteKings,
            blackKings + pair,
    )

    companion object {
        const val STEPS = 3
    }
}
