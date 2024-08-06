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

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.colorResource
import pl.org.seva.checkers.game.GameState
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Pieces(gameState: GameState, onTouchListener: (MotionEvent) -> Boolean) {

    val whiteColor = colorResource(com.google.android.material.R.color.material_dynamic_primary80)
    val blackColor = colorResource(com.google.android.material.R.color.material_dynamic_primary40)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { onTouchEvent ->
                onTouchListener(onTouchEvent)
            }
    ) {
        val dx = size.width / 8f
        val dy = size.height / 8f

        val radius = min(dx, dy) / 2 * 0.85f
        gameState.whiteMen.forEach { man ->
            translate(
                -size.width / 2 + man.first * dx + dx / 2,
                -size.height / 2 + man.second * dy + dy / 2
            ) {
                drawCircle(whiteColor, radius)
            }
        }
        gameState.blackMen.forEach { man ->
            translate(
                -size.width / 2 + man.first * dx + dx / 2,
                -size.height / 2 + man.second * dy + dy / 2
            ) {
                drawCircle(blackColor, radius)
            }
        }
        gameState.whiteKings.forEach { king ->
            translate(
                -size.width / 2 + king.first * dx + dx / 2,
                -size.height / 2 + king.second * dy + dy / 2
            ) {
                drawCircle(whiteColor, radius)
                drawCircle(Color.White, radius / 2)
            }
        }
        gameState.blackKings.forEach { king ->
            translate(
                -size.width / 2 + king.first * dx + dx / 2,
                -size.height / 2 + king.second * dy + dy / 2
            ) {
                drawCircle(blackColor, radius)
                drawCircle(Color.White, radius / 2)
            }
        }
        if (gameState.movingWhiteMan != -1 to -1) {
            translate(
                gameState.movingWhiteMan.first.toFloat(),
                gameState.movingWhiteMan.second.toFloat() - dy
            ) {
                drawCircle(whiteColor, radius)
            }
        }
        if (gameState.movingWhiteKing != -1 to -1) {
            translate(
                gameState.movingWhiteKing.first.toFloat(),
                gameState.movingWhiteKing.second.toFloat() - dy
            ) {
                drawCircle(whiteColor, radius)
                drawCircle(Color.White, radius / 2)
            }
        }
    }

}

