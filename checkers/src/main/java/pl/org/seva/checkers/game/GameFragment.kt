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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import pl.org.seva.checkers.R
import pl.org.seva.checkers.databinding.FrGameBinding
import kotlin.math.abs

class GameFragment : Fragment(R.layout.fr_game) {

    private lateinit var binding: FrGameBinding
    private val vm by viewModels<GameVM>()

    var isInMovement = false
    var isKingInMovement = false
    var pickedFrom = -1 to -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        binding = FrGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fun predecessor(x1: Int, y1: Int, x2: Int, y2: Int): Pair<Int, Int> {
            if (abs(x2 - x1) != abs(y2 - y1) ||
                    abs(x2 - x1) < 2 || abs(y2 - y1) < 2) return -1 to -1
            val dirx = if (x2 - x1 > 0) 1 else -1
            val diry = if (y2 - y1 > 0) 1 else -1
            return x2 - dirx to y2 - diry
        }

        fun validateKingMove(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
            val dirx = if (x2 - x1 > 0) 1 else -1
            val diry = if (y2 - y1 > 0) 1 else -1
            var x = x1 + dirx
            var y = y1 + diry
            if (x == x2 && y == y2) return true // movement by 1
            while (x != x2 - dirx && y != y2 - diry) {
                if (!vm.isEmpty(x, y)) return false
                x += dirx
                y += diry
            }
            return vm.isEmpty(x, y) || vm.removeBlack(x to y)
        }

        binding.pieces.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> if (vm.isWhiteMoving) {
                    val x = binding.pieces.getX(event.rawX)
                    val y = binding.pieces.getY(event.rawY)
                    vm.storeState()
                    pickedFrom = x to y
                    isKingInMovement = vm.containsWhiteKing(x, y)
                    isInMovement = vm.removeWhite(x, y)
                }
                MotionEvent.ACTION_MOVE -> if (isInMovement) {
                    if (isKingInMovement) {
                        vm.moveWhiteKingTo(event.rawX.toInt(), event.rawY.toInt())
                    }
                    else {
                        vm.moveWhiteManTo(event.rawX.toInt(), event.rawY.toInt())
                    }
                }
                MotionEvent.ACTION_UP -> if (isInMovement) {
                    val x = binding.pieces.getX(event.rawX)
                    val y = binding.pieces.getY(event.rawY)
                    if (pickedFrom != x to y && x in 0..7 && y in 0..7 && vm.isEmpty(x, y) &&
                        abs(x - pickedFrom.first) == 1 &&
                        y == pickedFrom.second - 1 ||
                        (abs(x - pickedFrom.first) == 2 &&
                                y == pickedFrom.second - 2) &&
                                vm.removeBlack(predecessor(pickedFrom.first, pickedFrom.second, x, y)) ||
                        abs(x - pickedFrom.first) == abs(y - pickedFrom.second) &&
                        isKingInMovement && validateKingMove(pickedFrom.first, pickedFrom.second, x, y)) {
                            vm.stopMovement()
                            vm.addWhite(x, y, isKingInMovement)
                            vm.commitState()
                            if (vm.whiteWon()) {
                                vm.setWhiteWon()
                            }
                            else {
                                vm.blackMove()
                            }
                    }
                    else {
                        vm.restoreState()
                    }
                    isKingInMovement = false
                    isInMovement = false
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reset -> {
                vm.reset()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
