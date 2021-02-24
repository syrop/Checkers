package pl.org.seva.checkers.game.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class Board(context: Context, attrs: AttributeSet) : View(context, attrs) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(canvas) {
            val dx = right / 8f
            val dy = bottom / 8f
            repeat(8) { x ->
                repeat(8) { y ->
                    if (x % 2 != y % 2) {
                        drawRect(x * dx, y * dy, (x + 1) * dx, (y + 1) * dy, BLACK_FILL)
                    }
                }
            }
        }
    }

    companion object {
        private val BLACK_FILL = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }
}
