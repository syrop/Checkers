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

package pl.org.seva.checkers.ui.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.toSize
import pl.org.seva.checkers.ui.model.PiecesUiModel
import kotlin.math.abs
import kotlin.math.min

@Composable
fun Pieces(
    piecesModel: PiecesUiModel,
    onStoreState: () -> Unit,
    onValidMove: (Int, Int, Int, Int, Int, Int, Boolean) -> Unit,
    onInvalidMove: () -> Unit,
) {

    var size by remember { mutableStateOf(Size.Zero) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val whiteColor = colorResource(com.google.android.material.R.color.material_dynamic_primary80)
    val blackColor = colorResource(com.google.android.material.R.color.material_dynamic_primary40)
    var dragX by remember { mutableStateOf(0) }
    var dragY by remember { mutableStateOf(0) }
    var movingWhiteMan by remember { mutableStateOf(-1 to -1) }
    var movingWhiteKing by remember { mutableStateOf(-1 to -1) }

    var isInMovement by remember { mutableStateOf(false) }
    var isKingInMovement by remember { mutableStateOf(false) }
    var pickedFrom by remember { mutableStateOf(-1 to -1) }

    fun predecessor(x1: Int, y1: Int, x2: Int, y2: Int): Pair<Int, Int> {
        if (abs(x2 - x1) != abs(y2 - y1) ||
            abs(x2 - x1) < 2 || abs(y2 - y1) < 2) return -1 to -1
        val dirX = if (x2 - x1 > 0) 1 else -1
        val dirY = if (y2 - y1 > 0) 1 else -1
        return x2 - dirX to y2 - dirY
    }

    fun validateKingMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        val dirX = if (x2 - x1 > 0) 1 else -1
        val dirY = if (y2 - y1 > 0) 1 else -1
        var x = x1 + dirX
        var y = y1 + dirY
        if (x == x2 && y == y2) return true // movement by 1
        while (x != x2 - dirX && y != y2 - dirY) {
            if (!piecesModel.isEmpty(x to y)) {
                if (
                    piecesModel.containsBlack(x to y) &&
                    x + dirX == x2 &&
                    x + dirY == y2
                )
                    return false
            }
            x += dirX
            y += dirY
        }
        return piecesModel.isEmpty(x to y)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it.toSize() }
            .pointerInput(piecesModel) {
                detectDragGestures(
                    onDragStart = { offset ->
                        offsetX = offset.x
                        offsetY = offset.y
                        dragX = (offset.x * 8 / size.width).toInt()
                        dragY = (offset.y * 8 / size.height).toInt()
                        if (piecesModel.containsWhite(dragX to dragY)) {
                            pickedFrom = dragX to dragY
                        }
                        isKingInMovement = piecesModel.whiteKings.contains(dragX to dragY)
                        isInMovement = piecesModel.containsWhite(dragX to dragY)
                        onStoreState()
                    },
                    onDragEnd = {
                        if (pickedFrom != dragX to dragY && dragX in 0..7 && dragY in 0..7 && piecesModel.isEmpty(
                                dragX to dragY
                            ) &&
                            abs(dragX - pickedFrom.first) == 1 &&
                            dragY == pickedFrom.second - 1 ||
                            (abs(dragX - pickedFrom.first) == 2 &&
                                    dragY == pickedFrom.second - 2) &&
                            piecesModel.containsBlack(
                                predecessor(
                                    pickedFrom.first,
                                    pickedFrom.second,
                                    dragX,
                                    dragY
                                )
                            ) ||
                            abs(dragX - pickedFrom.first) == abs(dragY - pickedFrom.second) &&
                            isKingInMovement && validateKingMove(
                                pickedFrom.first,
                                pickedFrom.second,
                                dragX,
                                dragY
                            )
                        ) {
                            val beating =
                                predecessor(pickedFrom.first, pickedFrom.second, dragX, dragY)
                            onValidMove(
                                pickedFrom.first,
                                pickedFrom.second,
                                beating.first,
                                beating.second,
                                dragX,
                                dragY,
                                isKingInMovement,
                            )
                        } else {
                            onInvalidMove()
                        }
                        movingWhiteMan = -1 to -1
                        movingWhiteKing = -1 to -1
                        pickedFrom = -1 to -1
                    },
                ) { _, dragAmount ->
                    val original = Offset(offsetX, offsetY)
                    val summed = original + dragAmount
                    val newValue = Offset(
                        x = summed.x.coerceIn(0f, size.width),
                        y = summed.y.coerceIn(0f, size.height)
                    )
                    offsetX = newValue.x
                    offsetY = newValue.y
                    if (pickedFrom != -1 to -1) {
                        if (isKingInMovement) {
                            movingWhiteKing = offsetX.toInt() to offsetY.toInt()
                        } else {
                            movingWhiteMan = offsetX.toInt() to offsetY.toInt()
                        }
                    }
                    dragX = (offsetX * 8 / size.width).toInt()
                    dragY = (offsetY * 8 / size.height).toInt()
                }
            }
    ) {
        val dx = size.width / 8f
        val dy = size.height / 8f

        val radius = min(dx, dy) / 2 * 0.85f
        piecesModel.whiteMen.filter { it != pickedFrom }.forEach { man ->
            translate(
                -size.width / 2 + man.first * dx + dx / 2,
                -size.height / 2 + man.second * dy + dy / 2
            ) {
                drawCircle(whiteColor, radius)
            }
        }
        piecesModel.blackMen.forEach { man ->
            translate(
                -size.width / 2 + man.first * dx + dx / 2,
                -size.height / 2 + man.second * dy + dy / 2
            ) {
                drawCircle(blackColor, radius)
            }
        }
        piecesModel.whiteKings.filter { it != pickedFrom }.forEach { king ->
            translate(
                -size.width / 2 + king.first * dx + dx / 2,
                -size.height / 2 + king.second * dy + dy / 2
            ) {
                drawCircle(whiteColor, radius)
                drawCircle(Color.White, radius / 2)
            }
        }
        piecesModel.blackKings.forEach { king ->
            translate(
                -size.width / 2 + king.first * dx + dx / 2,
                -size.height / 2 + king.second * dy + dy / 2
            ) {
                drawCircle(blackColor, radius)
                drawCircle(Color.White, radius / 2)
            }
        }
        if (movingWhiteMan != -1 to -1) {
            translate(
                movingWhiteMan.first.toFloat() - size.width / 2,
                movingWhiteMan.second.toFloat() - size.height / 2,
            ) {
                drawCircle(whiteColor, radius)
            }
        }
        if (movingWhiteKing != -1 to -1) {
            translate(
                movingWhiteKing.first.toFloat() - size.width / 2,
                movingWhiteKing.second.toFloat() - dy - size.height / 2,
            ) {
                drawCircle(whiteColor, radius)
                drawCircle(Color.White, radius / 2)
            }
        }
    }

}

