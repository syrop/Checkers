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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun Board() {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val dx = size.width / 8f
        val dy = size.height / 8f
        repeat(8) { x ->
            repeat(8) { y ->
                if (x % 2 != y % 2) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x * dx, y * dy),
                        size = size / 8f
                    )
                }
            }
        }
    }
}
