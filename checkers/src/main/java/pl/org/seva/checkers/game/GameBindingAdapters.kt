package pl.org.seva.checkers.game

import androidx.databinding.BindingAdapter
import pl.org.seva.checkers.game.view.Pieces

@BindingAdapter("gameState")
fun setGameState(pieces: Pieces, state: GameState) {
    pieces.setGameState(state)
}
