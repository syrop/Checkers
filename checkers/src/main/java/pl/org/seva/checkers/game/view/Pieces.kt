package pl.org.seva.checkers.game.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import pl.org.seva.checkers.R
import java.lang.Float.min

class Pieces(context: Context, attrs: AttributeSet) : View(context, attrs) {

        private val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.game,
                0,
                0,
        )

    private val whites = arrayOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
    private val blacks = arrayOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)

    private val whiteFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val blackFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        println("wiktor index count: ${a.indexCount}")
        val whiteMen = a.getString(R.styleable.game_white_men)
        val blackMen = a.getString(R.styleable.game_black_men)
        println("wiktor white: $whiteMen")
        println("wiktor black: $blackMen")

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
        }
    }
}