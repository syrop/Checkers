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

package pl.org.seva.checkers.game.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import pl.org.seva.checkers.game.GameState
import java.lang.Float.min

class Pieces(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var gameState = GameState(emptyList(), emptyList(), emptyList(), emptyList())

    fun setGameState(state: GameState) {
        gameState = state
        invalidate()
    }

    fun getX(x: Float) = x.toInt() * 8 / measuredWidth

    fun getY(y: Float) = y.toInt() * 8 / measuredHeight - 1

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with (canvas) {
            val dx = right / 8f
            val dy = bottom / 8f
            val radius = min(dx, dy) / 2 * 0.85f
            gameState.whiteMen.forEach { whiteMan ->
                drawCircle(whiteMan.first * dx + dx / 2, whiteMan.second * dy + dy / 2, radius, WHITE_FILL)
            }
            gameState.blackMen.forEach { blackMan ->
                drawCircle(blackMan.first * dx + dx / 2, blackMan.second * dy + dy / 2, radius, BLACK_FILL)
            }
            gameState.whiteKings.forEach { whiteKing ->
                drawCircle(whiteKing.first * dx + dx / 2, whiteKing.second * dy + dy / 2, radius, WHITE_FILL)
                drawCircle(whiteKing.first * dx + dx / 2, whiteKing.second * dy + dy / 2, radius / 2, CROWN)
            }
            gameState.blackKings.forEach { blackKing ->
                drawCircle(blackKing.first * dx + dx / 2, blackKing.second * dy + dy / 2, radius, BLACK_FILL)
                drawCircle(blackKing.first * dx + dx / 2, blackKing.second * dy + dy / 2, radius / 2, CROWN)
            }
            if (gameState.movingWhiteMan != -1 to -1) {
                drawCircle(gameState.movingWhiteMan.first.toFloat(), gameState.movingWhiteMan.second.toFloat() - dy, radius, WHITE_FILL)
            }
            if (gameState.movingWhiteKing != -1 to -1) {
                drawCircle(gameState.movingWhiteKing.first.toFloat(), gameState.movingWhiteKing.second.toFloat() - dy, radius, WHITE_FILL)
                drawCircle(gameState.movingWhiteKing.first.toFloat(), gameState.movingWhiteKing.second.toFloat() - dy, radius / 2, CROWN)
            }
        }
    }

    companion object {
        private val WHITE_FILL = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        private val BLACK_FILL = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        private val CROWN = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }
}
