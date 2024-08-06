/*
 * Copyright (C) 2021 Wiktor Nizio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.org.seva.checkers.game

import android.view.View
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameVM : ViewModel() {

    var isWhiteMoving = true

    var sizeX = 0
    var sizeY = 0

    private lateinit var storedState: GameState

    var gameState by mutableStateOf(GameState(WHITE_START_POSITION, BLACK_START_POSITION, emptyList(), emptyList()))

    var progressVisibility by mutableStateOf(false)

    var whiteWon by mutableStateOf(false)
    var blackWon by mutableStateOf(false)

    fun getX(x: Float) = x.toInt() * 8 / sizeX

    fun getY(y: Float) = y.toInt() * 8 / sizeY - 1

    fun reset() {
        isWhiteMoving = true
        progressVisibility = false
        whiteWon = false
        blackWon = false
        gameState = GameState(WHITE_START_POSITION, BLACK_START_POSITION, emptyList(), emptyList())
    }

    fun containsWhiteKing(x: Int, y: Int) = gameState.containsWhiteKing(x to y)

    fun removeWhite(x: Int, y: Int): Boolean {
        val removed = gameState.removeWhite(x to y)
        val result = removed != gameState
        gameState = removed
        return result
    }

    fun addWhite(x: Int, y: Int, forceKing: Boolean = false) {
        gameState = if (forceKing || y == 0) {
            gameState.addWhiteKing(x to y)
        }
        else {
            gameState.addWhiteMan(x to y)
        }
    }

    fun moveWhiteManTo(x: Int, y: Int) {
        gameState = gameState.copy(movingWhiteMan = x to y)
    }

    fun moveWhiteKingTo(x: Int, y: Int) {
        gameState = gameState.copy(movingWhiteKing = x to y)
    }

    fun stopMovement() {
        gameState = gameState.stopMovement()
    }

    fun removeBlack(pair: Pair<Int, Int>): Boolean {
        val removed = gameState.removeBlack(pair)
        val result = gameState != removed
        gameState = removed
        return result
    }

    fun isEmpty(x: Int, y: Int) = gameState.isEmpty(x to y)

    fun blackMove() {
        isWhiteMoving = false
        progressVisibility = true
        viewModelScope.launch {
            gameState = gameState.nextBlackMove()
            progressVisibility = false
            if (blackWon()) {
                blackWon = true
            }
            else {
                isWhiteMoving = true
            }
        }
    }

    fun storeState() {
        storedState = gameState
    }

    fun restoreState() {
        gameState = storedState
    }

    fun commitState() {
        gameState = storedState.getChildOrNull(gameState)
            ?.apply { reduceLevel() } ?: gameState
    }

    fun whiteWon() = gameState.whiteWon()

    fun setWhiteWon() {
        whiteWon = true
    }

    private fun blackWon() = gameState.blackWon()

    companion object {
        val WHITE_START_POSITION = listOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        val BLACK_START_POSITION = listOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
    }
}
