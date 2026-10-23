package pl.org.seva.checkers.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.BackgroundExecutingUseCase
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

class BlackMoveUseCase(
    private val piecesRepository: PiecesRepository,
) : BackgroundExecutingUseCase<Unit, PiecesDomainModel>() {

    override suspend fun executeInBackground(request: Unit): PiecesDomainModel = withContext(Dispatchers.Default) {

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
            diry: Int,
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
            } else { // capturing black
                if (containsBlack(x to y) && isValidAndEmpty(x + dirx to y + diry)) {
                    result.add(removeBlack(x to y).addWhiteKing(x + dirx to y + diry))
                }
            }
            return result
        }

        fun PiecesDomainModel.getChildren(): Iterable<PiecesDomainModel> {
            val result = mutableSetOf<PiecesDomainModel>()

            if (level != DEPTH - 1) {
                if (level % 2 == 0) { // black moves
                    blackMen.forEach { blackMan ->
                        val removed = removeBlack(blackMan)
                        val blackManMovesLeft = blackMan.first - 1 to blackMan.second + 1
                        if (isValidAndEmpty(blackManMovesLeft)) {
                            result.add(removed.addBlack(blackManMovesLeft))
                        }
                        val blackManMovesRight = blackMan.first + 1 to blackMan.second + 1
                        if (isValidAndEmpty(blackManMovesRight)) {
                            result.add(removed.addBlack(blackManMovesRight))
                        }
                        val blackManCapturesLeft = blackMan.first - 2 to blackMan.second + 2
                        if (isValidAndEmpty(blackManCapturesLeft) && containsWhite(blackManMovesLeft)) {
                            result.add(
                                removed.removeWhite(blackManMovesLeft).addBlack(blackManCapturesLeft)
                            )
                        }
                        val blackManCapturesRight = blackMan.first + 2 to blackMan.second + 2
                        if (isValidAndEmpty(blackManCapturesRight) && containsWhite(blackManMovesRight)) {
                            result.add(
                                removed.removeWhite(blackManMovesRight).addBlack(blackManCapturesRight)
                            )
                        }
                    }
                    blackKings.forEach { blackKing ->
                        val removed = removeBlack(blackKing)
                        result.addAll(removed.kingMoves(blackKing, -1, -1))
                        result.addAll(removed.kingMoves(blackKing, -1, +1))
                        result.addAll(removed.kingMoves(blackKing, +1, -1))
                        result.addAll(removed.kingMoves(blackKing, +1, +1))
                    }
                } else { // white moves
                    whiteMen.forEach { whiteMan ->
                        val removed = removeWhite(whiteMan)
                        val whiteManMovesLeft = whiteMan.first - 1 to whiteMan.second - 1
                        if (isValidAndEmpty(whiteManMovesLeft)) {
                            result.add(removed.addWhite(whiteManMovesLeft))
                        }
                        val whiteManMovesRight = whiteMan.first + 1 to whiteMan.second - 1
                        if (isValidAndEmpty(whiteManMovesRight)) {
                            result.add(removed.addWhite(whiteManMovesRight))
                        }
                        val whiteManCapturesLeft = whiteMan.first - 2 to whiteMan.second - 2
                        if (isValidAndEmpty(whiteManCapturesLeft) && containsBlack(whiteManMovesLeft)) {
                            result.add(
                                removed.removeBlack(whiteManMovesLeft).addWhite(whiteManCapturesLeft)
                            )
                        }
                        val whiteManCapturesRight = whiteMan.first + 2 to whiteMan.second - 2
                        if (isValidAndEmpty(whiteManCapturesRight) && containsBlack(whiteManMovesRight)) {
                            result.add(
                                removed.removeBlack(whiteManMovesRight).addWhite(whiteManCapturesRight)
                            )
                        }
                    }
                    whiteKings.forEach { whiteKing ->
                        val removed = removeWhite(whiteKing)
                        result.addAll(removed.kingMoves(whiteKing, -1, -1))
                        result.addAll(removed.kingMoves(whiteKing, -1, +1))
                        result.addAll(removed.kingMoves(whiteKing, +1, -1))
                        result.addAll(removed.kingMoves(whiteKing, +1, +1))
                    }
                }
            }
            return result.map { it.copy(level = level + 1, parent = this@getChildren.id) }
        }

        repeat(DEPTH) { level ->
            ensureActive()
            piecesRepository.getLeaves(level)
                .map { leaf ->
                    async {
                        piecesRepository[leaf].getChildren().forEach { child ->
                            piecesRepository.addLeaf(child)
                        }
                    }
                }.awaitAll()
        }

        piecesRepository.getLeaves().map { piecesRepository[it] }.forEach { leaf ->
            ensureActive()
            val heuristics = if (leaf.blackWon()) Int.MIN_VALUE else if (leaf.whiteWon()) Int.MAX_VALUE else
                leaf.whiteMen.size + leaf.whiteKings.size * KINGS_WEIGHT -
                    leaf.blackMen.size - leaf.blackKings.size * KINGS_WEIGHT
            val path = mutableListOf(leaf.copy(heuristics = heuristics))
            val visited = mutableSetOf(leaf.id)
            while (path.last().parent.isNotEmpty() && visited.add(path.last().parent)) {
                val parent = piecesRepository[path.last().parent]
                if (parent.level % 2 == 0) {  // black moves
                    if (parent.heuristics != null && parent.heuristics < heuristics) {
                        break
                    } else {
                        path.add(parent.copy(heuristics = heuristics))
                    }
                } else {  // white moves
                    if (parent.heuristics != null && parent.heuristics > heuristics) {
                        break
                    } else {
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
