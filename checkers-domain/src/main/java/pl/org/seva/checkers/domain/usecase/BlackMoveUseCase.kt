package pl.org.seva.checkers.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.BackgroundExecutingUseCase
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository
import java.util.UUID

class BlackMoveUseCase(
    private val piecesRepository: PiecesRepository,
) : BackgroundExecutingUseCase<Unit, PiecesDomainModel>() {

    fun PiecesDomainModel.whiteWon() = blackMen.isEmpty() && blackKings.isEmpty()

    fun PiecesDomainModel.blackWon() = whiteMen.isEmpty() && whiteKings.isEmpty()

    fun PiecesDomainModel.removeWhite(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        whiteMen = whiteMen.filter { it != pair },
        whiteKings = whiteKings.filter { it != pair },
    )

    fun PiecesDomainModel.removeBlack(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        blackMen = blackMen.filter { it != pair },
        blackKings = blackKings.filter { it != pair },
    )

    fun PiecesDomainModel.containsBlack(pair: Pair<Int, Int>) =
        blackMen.contains(pair) || blackKings.contains(pair)

    fun PiecesDomainModel.containsWhite(pair: Pair<Int, Int>) =
        whiteMen.contains(pair) || whiteKings.contains(pair)

    fun PiecesDomainModel.isEmpty(pair: Pair<Int, Int>) =
        !this.containsWhite(pair) && !containsBlack(pair)

    fun PiecesDomainModel.addWhiteMan(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        whiteMen = whiteMen + pair,
    )

    fun PiecesDomainModel.addBlackMan(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        blackMen = blackMen + pair,
    )

    fun PiecesDomainModel.addWhiteKing(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        whiteKings = whiteKings + pair,
    )

    fun PiecesDomainModel.addBlackKing(pair: Pair<Int, Int>) = copy(
        id = UUID.randomUUID().toString(),
        blackKings = blackKings + pair,
    )

    override suspend fun executeInBackground(request: Unit) = coroutineScope {

        fun PiecesDomainModel.isValidAndEmpty(pair: Pair<Int, Int>) =
            pair.first in 0..7 && pair.second in 0..7 && isEmpty(pair)

        fun PiecesDomainModel.addBlack(pair: Pair<Int, Int>) = if (pair.second == 7) {
            addBlackKing(pair)
        } else {
            addBlackMan(pair)
        }

        fun PiecesDomainModel.addWhite(pair: Pair<Int, Int>) = if (pair.second == 0) {
            addWhiteKing(pair)
        } else {
            addWhiteMan(pair)
        }

        fun PiecesDomainModel.kingMoves(
            pair: Pair<Int, Int>,
            dirx: Int,
            diry: Int
        ): List<PiecesDomainModel> {

            val result = mutableListOf<PiecesDomainModel>()
            var (x, y) = pair
            while (true) {
                x += dirx
                y += diry
                if (isValidAndEmpty(x to y)) {
                    result.add(if (level % 2 == 0) addBlackKing(x to y) else addWhiteKing(x to y))
                } else break
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

        suspend fun PiecesDomainModel.getChildren(): Iterable<PiecesDomainModel> = coroutineScope {

            val result = mutableSetOf<PiecesDomainModel>()

            if (level != DEPTH - 1) {
                if (level % 2 == 0) { // black moves
                    val men = async(Dispatchers.Default) {
                        mutableListOf<PiecesDomainModel>().apply {
                            blackMen
                                .map { blackMan -> // asynchronously list of moves of this one man
                                    async(Dispatchers.Default) {
                                        val removed = removeBlack(blackMan)
                                        mutableListOf<PiecesDomainModel>().apply {
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
                                .forEach { addAll(it) }
                        }
                    }
                    val kings = async(Dispatchers.Default) {
                        mutableListOf<PiecesDomainModel>().apply {
                            blackKings
                                .map { blackKing ->
                                    async(Dispatchers.Default) {
                                        val removed = removeBlack(blackKing)
                                        mutableListOf<PiecesDomainModel>().apply {
                                            addAll(removed.kingMoves(blackKing, -1, -1))
                                            addAll(removed.kingMoves(blackKing, -1, +1))
                                            addAll(removed.kingMoves(blackKing, +1, -1))
                                            addAll(removed.kingMoves(blackKing, +1, +1))
                                        }
                                    }
                                }
                                .map { it.await() }
                                .forEach { addAll(it) }
                        }
                    }
                    result.addAll(men.await())
                    result.addAll(kings.await())
                } else { // white moves
                    val men = async(Dispatchers.Default) {
                        mutableListOf<PiecesDomainModel>().apply {
                            whiteMen
                                .map { whiteMan -> // asynchronously list of moves of this one man
                                    async(Dispatchers.Default) {
                                        val removed = removeWhite(whiteMan)
                                        mutableListOf<PiecesDomainModel>().apply {
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
                                .forEach { addAll(it) }
                        }
                    }
                    val kings = async(Dispatchers.Default) {
                        mutableListOf<PiecesDomainModel>().apply {
                            whiteKings
                                .map { whiteKing ->
                                    async(Dispatchers.Default) {
                                        val removed = removeWhite(whiteKing)
                                        mutableListOf<PiecesDomainModel>().apply {
                                            addAll(removed.kingMoves(whiteKing, -1, -1))
                                            addAll(removed.kingMoves(whiteKing, -1, +1))
                                            addAll(removed.kingMoves(whiteKing, +1, -1))
                                            addAll(removed.kingMoves(whiteKing, +1, +1))
                                        }
                                    }
                                }
                                .map { it.await() }
                                .forEach { addAll(it) }
                        }
                    }
                    result.addAll(men.await())
                    result.addAll(kings.await())
                }
            }
            result.map { it.copy(level = level + 1, parent = this@getChildren.id) }
        }

        if (piecesRepository.getLeaves().toSet().first() == piecesRepository.root) {
            repeat(DEPTH) { level ->
                piecesRepository.getLeaves(level)
                    .map { leaf ->
                        async {
                            piecesRepository[leaf].getChildren().forEach { child ->
                                piecesRepository.addLeaf(child)
                            }
                        }
                    }.awaitAll()
            }
        }
        else {
            piecesRepository.getLeaves(DEPTH - 1)
                .map { leaf ->
                    async {
                        piecesRepository[leaf].getChildren().forEach { child ->
                            piecesRepository.addLeaf(child)
                        }
                    }
                }.awaitAll()
        }

        piecesRepository.getLeaves().map { piecesRepository[it] }.forEach { leaf ->
            val heuristics = if (leaf.blackWon()) Int.MIN_VALUE else if (leaf.whiteWon()) Int.MAX_VALUE else
                leaf.whiteMen.size + leaf.whiteKings.size * KINGS_WEIGHT -
                    leaf.blackMen.size - leaf.blackKings.size * KINGS_WEIGHT
            val path = mutableListOf(leaf.copy(heuristics = heuristics))
            while (path.last().parent.isNotEmpty()) {
                val parent = piecesRepository[path.last().parent]
                if (parent.level % 2 == 0) {  // black moves
                    if (parent.heuristics != null && parent.heuristics < heuristics) {
                        break
                    }
                    else {
                        path.add(parent.copy(heuristics = heuristics))
                    }
                }
                else {  // white moves
                    if (parent.heuristics != null && parent.heuristics > heuristics) {
                        break
                    }
                    else {
                        path.add(parent.copy(heuristics = heuristics))
                    }
                }
            }
            path.forEach { piecesRepository[it.id] = it }
        }

        piecesRepository.getImmediateMoves()
            .minByOrNull { it.heuristics ?: Int.MAX_VALUE }
            ?.apply { piecesRepository.prune(id) }
            ?: piecesRepository[piecesRepository.root]
    }

    companion object {
        const val DEPTH = 4 // higher than 5 has a significant performance impact
        const val KINGS_WEIGHT = 4
    }

}
