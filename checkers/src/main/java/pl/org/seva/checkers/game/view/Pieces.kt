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

    private var whites = emptyList<Pair<Int, Int>>()

    private var blacks = emptyList<Pair<Int, Int>>()

    private var moving = -1 to -1

    private val whiteFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val blackFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    fun setGameState(state: GameState) {
        whites = state.whiteMen
        blacks = state.blackMen
        moving = state.moving
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
            for (piece in whites) {
                drawCircle(piece.first * dx + dx / 2, piece.second * dy + dy / 2, radius, whiteFill)
            }
            for (piece in blacks) {
                drawCircle(piece.first * dx + dx / 2, piece.second * dy + dy / 2, radius, blackFill)
            }
            if (moving != -1 to -1) {
                drawCircle(moving.first.toFloat(), moving.second.toFloat() - dy, radius, whiteFill)
            }
        }
    }
}
