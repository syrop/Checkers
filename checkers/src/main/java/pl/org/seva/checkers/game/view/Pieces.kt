package pl.org.seva.checkers.game.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import pl.org.seva.checkers.game.GameState
import java.lang.Float.min

class Pieces(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var gameState = GameState(emptyList(), emptyList(), emptyList(), emptyList())

    private var movingWhiteMan = -1 to -1

    fun setGameState(state: GameState) {
        gameState = state
        movingWhiteMan = state.movingWhiteMan
        invalidate()
    }

    fun getX(x: Float) = x.toInt() * 8 / measuredWidth

    fun getY(y: Float) = y.toInt() * 8 / measuredHeight - 1

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with (canvas) {
            val dx = right / 8f
            val dy = bottom / 8f
            val radius = min(dx, dy) / 2 * 0.85f
            for (piece in gameState.whiteMen) {
                drawCircle(piece.first * dx + dx / 2, piece.second * dy + dy / 2, radius, WHITE_FILL)
            }
            for (piece in gameState.blackMen) {
                drawCircle(piece.first * dx + dx / 2, piece.second * dy + dy / 2, radius, BLACK_FILL)
            }
            if (movingWhiteMan != -1 to -1) {
                drawCircle(movingWhiteMan.first.toFloat(), movingWhiteMan.second.toFloat() - dy, radius, WHITE_FILL)
            }
        }
    }

    companion object {
        private val WHITE_FILL = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        private val BLACK_FILL = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.FILL
        }
    }
}
