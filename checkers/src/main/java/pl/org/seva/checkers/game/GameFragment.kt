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
        binding.viewModel = vm
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
                        (abs(x - pickedFrom.first) == 1 &&
                            y == pickedFrom.second - 1 ||
                                (abs(x - pickedFrom.first) == 2 &&
                                    y == pickedFrom.second - 2 ||
                                        abs(x - pickedFrom.first) == abs(y - pickedFrom.second) &&
                                        isKingInMovement) &&
                                    vm.removeBlack(predecessor(pickedFrom.first, pickedFrom.second, x, y)))) {
                                        vm.stopMovement()
                                        vm.addWhite(x, y, isKingInMovement)
                                        vm.commitState()
                                        vm.blackMove()
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
