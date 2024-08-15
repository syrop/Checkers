package pl.org.seva.checkers.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.given
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
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            listOf(0 to 0),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteMenPosition.whiteWon()
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
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            listOf(0 to 0),
            emptyList(),
            emptyList(),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteMenPosition.blackWon()
        }

        // Then
        assertTrue(actualResult)
    }

    @Test
    fun `Given only white kings when whiteWon then returns true`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            listOf(0 to 0),
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
    fun `Given only black kings when whiteWon then returns false`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            listOf(0 to 0),
            emptyList(),
            emptyList(),
            listOf(0 to 0),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteMenPosition.whiteWon()
        }

        // Then
        assertFalse(actualResult)
    }

    @Test
    fun `Given only white kings when blackWon then returns false`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            listOf(0 to 0),
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
    fun `Given only black kings when blackWon then returns true`() {
        // Given
        val givenWhiteMenPosition = PiecesDomainModel(
            UUID.randomUUID().toString(),
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            listOf(0 to 0),
        )

        // When
        val actualResult = with (classUnderTest) {
            givenWhiteMenPosition.blackWon()
        }

        // Then
        assertTrue(actualResult)
    }

}
