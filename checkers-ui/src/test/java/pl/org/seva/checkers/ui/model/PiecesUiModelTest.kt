package pl.org.seva.checkers.ui.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PiecesUiModelTest {

    @Test
    fun `Given white man when moving one step diagonally forward to empty cell then returns true`() {
        val model = PiecesUiModel(
            whiteMen = listOf(2 to 5),
            blackMen = emptyList(),
            whiteKings = emptyList(),
            blackKings = emptyList(),
        )

        assertTrue(model.isValidWhiteMove(2 to 5, 1 to 4, isKing = false))
        assertTrue(model.isValidWhiteMove(2 to 5, 3 to 4, isKing = false))
        assertFalse(model.isValidWhiteMove(2 to 5, 1 to 6, isKing = false))
        assertFalse(model.isValidWhiteMove(2 to 5, 2 to 5, isKing = false))
    }

    @Test
    fun `Given white man when jumping over black piece into empty cell then returns true and predecessor returns black cell`() {
        val model = PiecesUiModel(
            whiteMen = listOf(2 to 5),
            blackMen = listOf(3 to 4),
            whiteKings = emptyList(),
            blackKings = emptyList(),
        )

        assertTrue(model.isValidWhiteMove(2 to 5, 4 to 3, isKing = false))
        assertEquals(3 to 4, PiecesUiModel.predecessor(2, 5, 4, 3))
        assertFalse(model.isValidWhiteMove(2 to 5, 0 to 3, isKing = false))
    }

    @Test
    fun `Given white king when sliding or capturing single black piece at end of clear diagonal then returns true`() {
        val model = PiecesUiModel(
            whiteMen = listOf(1 to 6),
            blackMen = listOf(4 to 3),
            whiteKings = listOf(1 to 0),
            blackKings = emptyList(),
        )

        assertTrue(model.isValidWhiteMove(1 to 0, 3 to 2, isKing = true))
        assertTrue(model.isValidWhiteMove(1 to 0, 5 to 4, isKing = true))
        assertFalse(model.isValidWhiteMove(1 to 0, 6 to 5, isKing = true))
    }
}
