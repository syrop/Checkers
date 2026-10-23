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

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import pl.org.seva.checkers.ui.mapper.PiecesPresentationToUiMapper
import pl.org.seva.checkers.ui.view.GameViewModel
import pl.org.seva.checkers.ui.view.Pieces
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var piecesPresentationToUiMapper: PiecesPresentationToUiMapper

    private val vm by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckersTheme {
                GameScreen(
                    vm = vm,
                    piecesPresentationToUiMapper = piecesPresentationToUiMapper,
                )
            }
        }
    }
}

@Composable
private fun CheckersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    vm: GameViewModel,
    piecesPresentationToUiMapper: PiecesPresentationToUiMapper,
) {
    val state by vm.viewState.collectAsStateWithLifecycle()
    val piecesUiModel = remember(state.pieces, piecesPresentationToUiMapper) {
        piecesPresentationToUiMapper.toUi(state.pieces)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.game_label)) },
                actions = {
                    TextButton(onClick = { vm.reset() }) {
                        Text(text = stringResource(R.string.reset))
                    }
                },
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Pieces(
                piecesModel = piecesUiModel,
                onStoreState = { vm.storeState() },
                onValidMove = { x1, y1, beatingX, beatingY, x2, y2, isKing ->
                    vm.removeWhite(x1 to y1)
                    vm.removeBlack(beatingX to beatingY)
                    vm.addWhite(x2, y2, isKing)
                },
                onInvalidMove = { vm.restoreState() },
                enabled = !state.isLoading && !state.whiteWon && !state.blackWon,
            )
            if (state.isLoading) {
                CircularProgressIndicator()
            }
            if (state.whiteWon) {
                Text(
                    text = stringResource(R.string.white_won),
                    fontSize = 34.sp,
                )
            }
            if (state.blackWon) {
                Text(
                    text = stringResource(R.string.black_won),
                    fontSize = 34.sp,
                )
            }
        }
    }
}
