package pl.org.seva.checkers.game

import androidx.databinding.BindingAdapter
import pl.org.seva.checkers.game.view.Pieces

@BindingAdapter("data")
fun setData(pieces: Pieces, data: GameData) {
    pieces.setGameData(data)
}
