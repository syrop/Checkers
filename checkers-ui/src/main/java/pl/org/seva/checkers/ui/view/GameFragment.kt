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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import pl.org.seva.checkers.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import pl.org.seva.checkers.ui.mapper.PiecesPresentationToUiMapper
import javax.inject.Inject

@AndroidEntryPoint
class GameFragment : Fragment() {

    @Inject
    lateinit var piecesPresentationToUiMapper: PiecesPresentationToUiMapper

    private val vm by viewModels<GameViewModel>()

    private var isKingInMovement = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        return ComposeView(requireContext()).apply {
            setContent {
                    Box {
                        Board()
                        Pieces(
                            piecesPresentationToUiMapper.toUi(vm.viewState.collectAsState().value.pieces),
                            onStoreState = {
                                vm.storeState()
                            },
                            onValidMove = { x1, y1, beatingX, beatingY, x2, y2, isKing ->
                                vm.removeWhite(x1 to y1)
                                vm.removeBlack(beatingX to beatingY)
                                vm.addWhite(x2, y2, isKing)
                                if (vm.viewState.value.pieces.blackMen.toSet().isEmpty() &&
                                    vm.viewState.value.pieces.blackKings.toSet().isEmpty()) {
                                    vm.setWhiteWon()
                                }
                            },
                            onInvalidMove = {
                                vm.restoreState()
                            }
                        )
                        if (vm.viewState.collectAsState().value.isLoading) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        if (vm.whiteWon.collectAsState().value) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = getString(R.string.white_won),
                                    fontSize = 34.sp,
                                )
                            }
                        }
                        if (vm.blackWon.collectAsState().value) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = getString(R.string.black_won),
                                    fontSize = 34.sp,
                                )
                            }
                        }
                    }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.fetchPieces()
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
