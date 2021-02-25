package pl.org.seva.checkers.game

import kotlinx.coroutines.*

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

    suspend fun populateChildren(): Unit = coroutineScope {
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

        fun GameState.kingMoves(pair: Pair<Int, Int>, dirx: Int, diry: Int): List<GameState> {
            val result = mutableListOf<GameState>()
            var (x, y) = pair
            while (true) {
                x += dirx
                y += diry
                if (isValidField(x to y)) {
                    result.add(if (level % 2 == 0) addBlackKing(x to y) else addWhiteKing(x to y))
                }
                else break
            }
            if (level % 2 == 0) { // capturing white
                if (containsWhite(x to y) && isValidField(x + dirx to y + diry)) {
                    result.add(removeWhite(x to y).addWhiteKing(x + dirx to y + diry))
                }
            }
            else { // capturing black
                if (containsBlack(x to y) && isValidField(x + dirx to y + diry)) {
                    result.add(removeBlack(x to y).addBlackKing(x + dirx to y + diry))
                }
            }
            return result
        }

        if (level == STEPS) {
            return@coroutineScope
        }
        if (children.isEmpty()) {
            if (level % 2 == 0) { // black moves
                val men = async(Dispatchers.Default) {
                    blackMen
                        .map { blackMan -> // asynchronously list of moves of this one man
                            async(Dispatchers.Default) {
                                val removed = removeBlack(blackMan)
                                mutableListOf<GameState>().apply {
                                    val blackManMovesLeft =
                                        blackMan.first - 1 to blackMan.second + 1
                                    if (isValidField(blackManMovesLeft)) {
                                        add(removed.addBlack(blackManMovesLeft))
                                    }
                                    val blackManMovesRight =
                                        blackMan.first + 1 to blackMan.second + 1
                                    if (isValidField(blackManMovesRight)) {
                                        add(removed.addBlack(blackManMovesRight))
                                    }
                                    val blackManCapturesLeft =
                                        blackMan.first - 2 to blackMan.second + 2
                                    if (isValidField(blackManCapturesLeft) && containsWhite(
                                            blackManMovesLeft
                                        )
                                    ) {
                                        add(
                                            removed.removeWhite(blackManMovesLeft)
                                                .addBlack(blackManCapturesLeft)
                                        )
                                    }
                                    val blackManCapturesRight =
                                        blackMan.first + 2 to blackMan.second + 2
                                    if (isValidField(blackManCapturesRight) && containsWhite(
                                            blackManMovesRight
                                        )
                                    ) {
                                        add(
                                            removed.removeWhite(blackManMovesRight)
                                                .addBlack(blackManCapturesRight)
                                        )
                                    }
                                }
                            }
                        }
                        .map { it.await() } // list of moves of this one man
                        .reduce { acc, list -> acc.apply { addAll(list) } } // list of moves of all black men
                        .onEach { it.level = level + 1 }
                }
                val kings = async(Dispatchers.Default) {
                    blackKings
                        .map { blackKing ->
                            async(Dispatchers.Default) {
                                val removed = removeBlack(blackKing)
                                mutableListOf<GameState>().apply {
                                    addAll(removed.kingMoves(blackKing, -1, -1))
                                    addAll(removed.kingMoves(blackKing, -1, +1))
                                    addAll(removed.kingMoves(blackKing, +1, -1))
                                    addAll(removed.kingMoves(blackKing, +1, +1))
                                }
                            }
                        }
                        .map { it.await() }
                        .reduce { acc, list -> acc.apply { addAll(list) } } // list of moves of all black men
                        .onEach { it.level = level + 1 }
                }
                children.addAll(men.await())
                children.addAll(kings.await())
            } else { // white moves
                val men = async(Dispatchers.Default) {
                    whiteMen
                        .map { whiteMan -> // asynchronously list of moves of this one man
                            async(Dispatchers.Default) {
                                val removed = removeWhite(whiteMan)
                                mutableListOf<GameState>().apply {
                                    val whiteManMovesLeft =
                                        whiteMan.first - 1 to whiteMan.second - 1
                                    if (isValidField(whiteManMovesLeft)) {
                                        add(removed.addWhite(whiteManMovesLeft))
                                    }
                                    val whiteManMovesRight =
                                        whiteMan.first + 1 to whiteMan.second - 1
                                    if (isValidField(whiteManMovesRight)) {
                                        add(removed.addWhite(whiteManMovesRight))
                                    }
                                    val whiteManCapturesLeft =
                                        whiteMan.first - 2 to whiteMan.second - 2
                                    if (isValidField(whiteManCapturesLeft) && containsBlack(
                                            whiteManMovesLeft
                                        )
                                    ) {
                                        add(
                                            removed.removeBlack(whiteManMovesLeft)
                                                .addWhite(whiteManCapturesLeft)
                                        )
                                    }
                                    val whiteManCapturesRight =
                                        whiteMan.first + 2 to whiteMan.second - 2
                                    if (isValidField(whiteManCapturesRight) && containsBlack(
                                            whiteManMovesRight
                                        )
                                    ) {
                                        add(
                                            removed.removeBlack(whiteManMovesRight)
                                                .addWhite(whiteManCapturesRight)
                                        )
                                    }
                                }
                            }
                        }
                        .map { it.await() } // list of moves of this one man
                        .reduce { acc, list -> acc.apply { addAll(list) } } // list of moves of all white men
                        .onEach { it.level = level + 1 }
                }
                val kings = async(Dispatchers.Default) {
                    whiteKings
                        .map { whiteKing ->
                            async(Dispatchers.Default) {
                                val removed = removeWhite(whiteKing)
                                mutableListOf<GameState>().apply {
                                    addAll(removed.kingMoves(whiteKing, -1, -1))
                                    addAll(removed.kingMoves(whiteKing, -1, +1))
                                    addAll(removed.kingMoves(whiteKing, +1, -1))
                                    addAll(removed.kingMoves(whiteKing, +1, +1))
                                }
                            }
                        }
                        .map { it.await() }
                        .reduce { acc, list -> acc.apply { addAll(list) } } // list of moves of all black men
                        .onEach { it.level = level + 1 }
                }
                children.addAll(men.await())
                children.addAll(kings.await())
            }
        } // if (children.isEmpty())
        children
            .map { child ->
                launch(Dispatchers.Default) {
                    child.populateChildren()
                }
            }
            .joinAll()
    }

    fun isEmpty(pair: Pair<Int, Int>) = !containsWhite(pair) && !containsBlack(pair)

    private fun containsWhite(pair: Pair<Int, Int>) = whiteMen.contains(pair) || whiteKings.contains(pair)

    private fun containsBlack(pair: Pair<Int, Int>) = blackMen.contains(pair) || blackKings.contains(pair)

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

    private fun addBlackMan(pair: Pair<Int, Int>) = GameState (
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

    private fun addBlackKing(pair: Pair<Int, Int>) = GameState(
            whiteMen,
            blackMen,
            whiteKings,
            blackKings + pair,
    )

    companion object {
        const val STEPS = 3
    }
}
