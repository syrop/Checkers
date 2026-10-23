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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.util.fastForEach
import pl.org.seva.checkers.ui.model.PiecesUiModel
import kotlin.math.min

@Composable
fun Pieces(
    piecesModel: PiecesUiModel,
    onStoreState: () -> Unit,
    onValidMove: (Int, Int, Int, Int, Int, Int, Boolean) -> Unit,
    onInvalidMove: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val whiteColor = colorResource(com.google.android.material.R.color.material_dynamic_primary80)
    val blackColor = colorResource(com.google.android.material.R.color.material_dynamic_primary40)
    var dragX by remember { mutableIntStateOf(0) }
    var dragY by remember { mutableIntStateOf(0) }

    var isKingInMovement by remember { mutableStateOf(false) }
    var pickedFrom by remember { mutableStateOf(-1 to -1) }

    val currentOnStoreState by rememberUpdatedState(onStoreState)
    val currentOnValidMove by rememberUpdatedState(onValidMove)
    val currentOnInvalidMove by rememberUpdatedState(onInvalidMove)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .drawBoard()
            .pointerInput(piecesModel, enabled) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragStart = { offset ->
                        val width = size.width.toFloat()
                        val height = size.height.toFloat()
                        if (width <= 0f || height <= 0f) return@detectDragGestures
                        offsetX = offset.x
                        offsetY = offset.y
                        dragX = (offset.x * 8f / width).toInt().coerceIn(0, 7)
                        dragY = (offset.y * 8f / height).toInt().coerceIn(0, 7)
                        val startCell = dragX to dragY
                        if (piecesModel.containsWhite(startCell)) {
                            pickedFrom = startCell
                        }
                        isKingInMovement = piecesModel.whiteKings.contains(startCell)
                        currentOnStoreState()
                    },
                    onDragEnd = {
                        if (piecesModel.isValidWhiteMove(pickedFrom, dragX to dragY, isKingInMovement)) {
                            val beating = PiecesUiModel.predecessor(
                                pickedFrom.first,
                                pickedFrom.second,
                                dragX,
                                dragY,
                            )
                            currentOnValidMove(
                                pickedFrom.first,
                                pickedFrom.second,
                                beating.first,
                                beating.second,
                                dragX,
                                dragY,
                                isKingInMovement,
                            )
                        } else {
                            currentOnInvalidMove()
                        }
                        pickedFrom = -1 to -1
                    },
                    onDragCancel = {
                        currentOnInvalidMove()
                        pickedFrom = -1 to -1
                    },
                ) { _, dragAmount ->
                    val width = size.width.toFloat()
                    val height = size.height.toFloat()
                    if (width <= 0f || height <= 0f) return@detectDragGestures
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, width)
                    offsetY = (offsetY + dragAmount.y).coerceIn(0f, height)
                    dragX = (offsetX * 8f / width).toInt().coerceIn(0, 7)
                    dragY = (offsetY * 8f / height).toInt().coerceIn(0, 7)
                }
            }
    ) {
        val dx = size.width / 8f
        val dy = size.height / 8f
        val currentPickedFrom = pickedFrom
        val currentOffsetX = offsetX
        val currentOffsetY = offsetY

        val radius = min(dx, dy) / 2f * 0.85f
        piecesModel.whiteMen.fastForEach { man ->
            if (man != currentPickedFrom) {
                translate(
                    -size.width / 2f + man.first * dx + dx / 2f,
                    -size.height / 2f + man.second * dy + dy / 2f,
                ) {
                    drawCircle(whiteColor, radius)
                }
            }
        }
        piecesModel.blackMen.fastForEach { man ->
            translate(
                -size.width / 2f + man.first * dx + dx / 2f,
                -size.height / 2f + man.second * dy + dy / 2f,
            ) {
                drawCircle(blackColor, radius)
            }
        }
        piecesModel.whiteKings.fastForEach { king ->
            if (king != currentPickedFrom) {
                translate(
                    -size.width / 2f + king.first * dx + dx / 2f,
                    -size.height / 2f + king.second * dy + dy / 2f,
                ) {
                    drawCircle(whiteColor, radius)
                    drawCircle(Color.White, radius / 2f)
                }
            }
        }
        piecesModel.blackKings.fastForEach { king ->
            translate(
                -size.width / 2f + king.first * dx + dx / 2f,
                -size.height / 2f + king.second * dy + dy / 2f,
            ) {
                drawCircle(blackColor, radius)
                drawCircle(Color.White, radius / 2f)
            }
        }
        if (currentPickedFrom != -1 to -1) {
            translate(
                currentOffsetX - size.width / 2f,
                currentOffsetY - size.height / 2f,
            ) {
                drawCircle(whiteColor, radius)
                if (isKingInMovement) {
                    drawCircle(Color.White, radius / 2f)
                }
            }
        }
    }
}
