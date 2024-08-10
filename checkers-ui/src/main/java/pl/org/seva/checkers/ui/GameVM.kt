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

package pl.org.seva.checkers.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import pl.org.seva.checkers.ui.model.PiecesUiModel

class GameVM : ViewModel() {

    var isWhiteMoving = true

    var sizeX = 0
    var sizeY = 0

    private lateinit var storedState: PiecesUiModel

    private var piecesState by mutableStateOf(
        PiecesUiModel(
            WHITE_START_POSITION,
            BLACK_START_POSITION,
            emptyList(),
            emptyList()
        )
    )

    var isProgressVisible by mutableStateOf(false)

    var whiteWon by mutableStateOf(false)
    var blackWon by mutableStateOf(false)

    fun reset() {
        isWhiteMoving = true
        isProgressVisible = false
        whiteWon = false
        blackWon = false
        piecesState = PiecesUiModel(
            WHITE_START_POSITION,
            BLACK_START_POSITION,
            emptyList(),
            emptyList()
        )
    }

    fun storeState() {
        storedState = piecesState
    }

    fun restoreState() {
        piecesState = storedState
    }

    fun setWhiteWon() {
        whiteWon = true
    }

    companion object {
        val WHITE_START_POSITION = listOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        val BLACK_START_POSITION = listOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
    }
}
