package pl.org.seva.checkers.ui.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.org.seva.checkers.presentation.model.PiecesPresentationModel
import pl.org.seva.checkers.ui.model.PiecesUiModel

class PiecesPresentationToUiMapperTest {

    private val classUnderTest = PiecesPresentationToUiMapper()

    @Test
    fun `Given presentation model when toUi then maps all piece collections`() {
        val presentationModel = PiecesPresentationModel(
            whiteMen = listOf(0 to 7, 2 to 7),
            blackMen = listOf(1 to 0, 3 to 0),
            whiteKings = listOf(5 to 0),
            blackKings = listOf(4 to 7),
        )
        val expected = PiecesUiModel(
            whiteMen = listOf(0 to 7, 2 to 7),
            blackMen = listOf(1 to 0, 3 to 0),
            whiteKings = listOf(5 to 0),
            blackKings = listOf(4 to 7),
        )

        val actual = classUnderTest.toUi(presentationModel)

        assertEquals(expected, actual)
    }
}
