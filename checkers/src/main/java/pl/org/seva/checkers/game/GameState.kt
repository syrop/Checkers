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

    private var heuristic = 0

    private val children = mutableListOf<GameState>()

    private suspend fun populateChildren(): Unit = coroutineScope {
        fun isValidAndEmpty(pair: Pair<Int, Int>) = pair.first in 0..7 && pair.second in 0..7 && isEmpty(pair)

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
                if (isValidAndEmpty(x to y)) {
                    result.add(if (level % 2 == 0) addBlackKing(x to y) else addWhiteKing(x to y))
                }
                else break
            }
            if (level % 2 == 0) { // capturing white
                if (containsWhite(x to y) && isValidAndEmpty(x + dirx to y + diry)) {
                    result.add(removeWhite(x to y).addBlackKing(x + dirx to y + diry))
                }
            }
            else { // capturing black
                if (containsBlack(x to y) && isValidAndEmpty(x + dirx to y + diry)) {
                    result.add(removeBlack(x to y).addWhiteKing(x + dirx to y + diry))
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
                    mutableListOf<GameState>().apply {
                        blackMen
                            .map { blackMan -> // asynchronously list of moves of this one man
                                async(Dispatchers.Default) {
                                    val removed = removeBlack(blackMan)
                                    mutableListOf<GameState>().apply {
                                        val blackManMovesLeft =
                                            blackMan.first - 1 to blackMan.second + 1
                                        if (isValidAndEmpty(blackManMovesLeft)) {
                                            add(removed.addBlack(blackManMovesLeft))
                                        }
                                        val blackManMovesRight =
                                            blackMan.first + 1 to blackMan.second + 1
                                        if (isValidAndEmpty(blackManMovesRight)) {
                                            add(removed.addBlack(blackManMovesRight))
                                        }
                                        val blackManCapturesLeft =
                                            blackMan.first - 2 to blackMan.second + 2
                                        if (isValidAndEmpty(blackManCapturesLeft) && containsWhite(
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
                                        if (isValidAndEmpty(blackManCapturesRight) && containsWhite(
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
                            .onEach { addAll(it) }
                    }
                }
                val kings = async(Dispatchers.Default) {
                    mutableListOf<GameState>().apply {
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
                            .onEach { addAll(it) }
                    }
                }
                children.addAll(men.await())
                children.addAll(kings.await())
            } else { // white moves
                val men = async(Dispatchers.Default) {
                    mutableListOf<GameState>().apply {
                        whiteMen
                            .map { whiteMan -> // asynchronously list of moves of this one man
                                async(Dispatchers.Default) {
                                    val removed = removeWhite(whiteMan)
                                    mutableListOf<GameState>().apply {
                                        val whiteManMovesLeft =
                                            whiteMan.first - 1 to whiteMan.second - 1
                                        if (isValidAndEmpty(whiteManMovesLeft)) {
                                            add(removed.addWhite(whiteManMovesLeft))
                                        }
                                        val whiteManMovesRight =
                                            whiteMan.first + 1 to whiteMan.second - 1
                                        if (isValidAndEmpty(whiteManMovesRight)) {
                                            add(removed.addWhite(whiteManMovesRight))
                                        }
                                        val whiteManCapturesLeft =
                                            whiteMan.first - 2 to whiteMan.second - 2
                                        if (isValidAndEmpty(whiteManCapturesLeft) && containsBlack(
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
                                        if (isValidAndEmpty(whiteManCapturesRight) && containsBlack(
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
                            .onEach { addAll(it) }
                    }
                }
                val kings = async(Dispatchers.Default) {
                    mutableListOf<GameState>().apply {
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
                            .onEach { addAll(it) }
                    }
                }
                children.addAll(men.await())
                children.addAll(kings.await())
            }
            children.onEach { it.level = level +1 }
        } // if (children.isEmpty())
        children
            .map { child ->
                launch(Dispatchers.Default) {
                    child.populateChildren()
                }
            }
            .joinAll()
    }

    private fun updateHeuristic() {
        fun heuristic() = if (whiteMen .isEmpty() && whiteKings.isEmpty()) Int.MAX_VALUE
        else if (blackMen.isEmpty() && blackKings.isEmpty()) Int.MIN_VALUE else
            whiteMen.size + whiteKings.size * KINGS_WEIGHT - blackMen.size - blackKings.size * KINGS_WEIGHT
        heuristic = if (level == STEPS) {
            heuristic()
        } else {
            children.forEach { it.updateHeuristic() }
            when {
                children.isEmpty() -> heuristic()
                level % 2 == 0 -> { // black moves
                    children.maxOf { it.heuristic }
                }
                else -> { // white moves
                    children.minOf { it.heuristic }
                }
            }
        }
    }

    suspend fun nextBlackMove(): GameState {
        populateChildren()
        updateHeuristic()
        return children.minWithOrNull { s1, s2 ->
            when {
                s1.heuristic < s2.heuristic -> -1
                s1.heuristic == s2.heuristic -> 0
                else -> 1
            }
        } ?: GameState(emptyList(), emptyList(), emptyList(), emptyList())
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

    fun getChildOrNull(state: GameState): GameState? {
        val id = children.indexOf(state)
        return if (id >= 0) children[id] else null
    }

    fun reduceLevel() {
        level -= 2
        children.onEach { it.reduceLevel() }
    }

    fun containsWhiteKing(pair: Pair<Int, Int>) = whiteKings.contains(pair)

    companion object {
        const val STEPS = 4 // higher than 5 has a significant performance impact
        const val KINGS_WEIGHT = 4
    }
}
