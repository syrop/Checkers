package pl.org.seva.checkers.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository
import java.util.UUID


@RunWith(MockitoJUnitRunner::class)
class BlackMoveUseCaseTest {

    private lateinit var classUnderTest: BlackMoveUseCase

    @Mock
    private lateinit var piecesRepository: PiecesRepository

    @Before
    fun setUp() {
        classUnderTest = BlackMoveUseCase(piecesRepository)
    }

    @Test
    fun `Given only white men when whiteWon then returns true`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            listOf(0 to 0),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
             givenWhiteMenPosition.whiteWon()
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given only black men when whiteWon then returns false`() {
        // Given
        val givenBlackMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            listOf(0 to 0),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenBlackMenPosition.whiteWon()
        }

        // Then
        assertFalse(actualResult)
    }

    @Test
    fun `Given only white men when blackWon then returns false`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            listOf(0 to 0),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteMenPosition.blackWon()
        }

        // Then
        assertFalse(actualResult)
    }

    @Test
    fun `Given only black men when blackWon then returns true`() {
        // Given
        val givenBlackMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            listOf(0 to 0),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenBlackMenPosition.blackWon()
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given only white kings when whiteWon then returns true`() {
        // Given
        val givenWhiteKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            listOf(0 to 0),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteKingsPosition.whiteWon()
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given only black kings when whiteWon then returns false`() {
        // Given
        val givenBlackKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            listOf(0 to 0),
            emptyList(),
            emptyList(),
            listOf(0 to 0),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenBlackKingsPosition.whiteWon()
        }

        // Then
        assertFalse(actualResult)
    }

    @Test
    fun `Given only white kings when blackWon then returns false`() {
        // Given
        val givenWhiteKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            listOf(0 to 0),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteKingsPosition.blackWon()
        }

        // Then
        assertFalse(actualResult)
    }

    @Test
    fun `Given only black kings when blackWon then returns true`() {
        // Given
        val givenBlackKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            listOf(0 to 0),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenBlackKingsPosition.blackWon()
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given white men when removeWhite then returns one man less`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            listOf(0 to 0, 1 to 1),
            emptyList(),
            emptyList(),
            emptyList(),
        )
        val expectedResult = givenWhiteMenPosition.whiteMen.size - 1

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteMenPosition.removeWhite(0 to 0)
        }.whiteMen.size

        // Then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `Given white kings when removeWhite then returns one king less`() {
        // Given
        val givenWhiteKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            listOf(0 to 0, 1 to 1),
            emptyList(),
        )
        val expectedResult = givenWhiteKingsPosition.whiteKings.size - 1

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteKingsPosition.removeWhite(0 to 0)
        }.whiteKings.size

        // Then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `Given black men when removeBlack then returns one man less`() {
        // Given
        val givenBlackMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            listOf(0 to 0, 1 to 1),
            emptyList(),
            emptyList(),
        )
        val expectedResult = givenBlackMenPosition.blackMen.size - 1

        // When
        val actualResult = with (classUnderTest) {
            givenBlackMenPosition.removeBlack(0 to 0)
        }.blackMen.size

        // Then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `Given black kings when removeBlack then returns one king less`() {
        // Given
        val givenBlackKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            listOf(0 to 0, 1 to 1),
        )
        val expectedResult = givenBlackKingsPosition.blackKings.size - 1

        // When
        val actualResult = with (classUnderTest) {
            givenBlackKingsPosition.removeBlack(0 to 0)
        }.blackKings.size

        // Then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `Given white man when containsWhite returns true`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            listOf(0 to 0),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteMenPosition.containsWhite(0 to 0)
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given white king when containsWhite returns true`() {
        // Given
        val givenWhiteKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            listOf(0 to 0),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteKingsPosition.containsWhite(0 to 0)
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given black man when containsBlack returns true`() {
        // Given
        val givenBlackMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            listOf(0 to 0),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenBlackMenPosition.containsBlack(0 to 0)
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given black king when containsBlack returns true`() {
        // Given
        val givenBlackKingsPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            listOf(0 to 0),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenBlackKingsPosition.containsBlack(0 to 0)
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given empty position when isEmpty returns true`() {
        val givenEmptyPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenEmptyPosition.isEmpty(0 to 0)
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given empty position when addWhiteMan returns one man`() {
        // Given
        val givenEmptyPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenEmptyPosition.addWhiteMan(0 to 0)
        }.whiteMen.size

        // Then
        assertEquals(1, actualResult)
    }

    @Test
    fun `Given empty position when addWhiteKing returns one man`() {
        // Given
        val givenEmptyPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenEmptyPosition.addWhiteKing(0 to 0)
        }.whiteKings.size

        // Then
        assertEquals(1, actualResult)
    }

    @Test
    fun `Given empty position when addBlackMan returns one man`() {
        // Given
        val givenEmptyPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenEmptyPosition.addBlackMan(0 to 0)
        }.blackMen.size

        // Then
        assertEquals(1, actualResult)
    }

    @Test
    fun `Given empty position when addBlackKing returns one man`() {
        // Given
        val givenEmptyPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenEmptyPosition.addBlackKing(0 to 0)
        }.blackKings.size

        // Then
        assertEquals(1, actualResult)
    }


}
