package pl.org.seva.checkers.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
class FetchPiecesUseCaseTest {

    private lateinit var classUnderTest: FetchPiecesUseCase

    @Mock
    private lateinit var piecesRepository: PiecesRepository

    @Before
    fun setUp() {
        classUnderTest = FetchPiecesUseCase(piecesRepository)
    }

    @Test
    fun `Given start position when executeInBackground then returns pieces`() = runTest {
        // Given
        val givenWhiteMen = listOf(
            0 to 7,
            1 to 6,
            2 to 7,
            3 to 6,
            4 to 7,
            5 to 6,
            6 to 7,
            7 to 6,
            0 to 5,
            2 to 5,
            4 to 5,
            6 to 5,
        )
        val givenBlackMen = listOf(
            0 to 1,
            1 to 0,
            2 to 1,
            3 to 0,
            4 to 1,
            5 to 0,
            6 to 1,
            7 to 0,
            1 to 2,
            3 to 2,
            5 to 2,
            7 to 2,
        )
        val givenId = UUID.randomUUID().toString()
        val givenParentId = ""
        val givenWhiteKings = emptyList<Pair<Int, Int>>()
        val givenBlackKings = emptyList<Pair<Int, Int>>()
        val expectedPieces = PiecesDomainModel(
            givenId,
            givenParentId,
            givenWhiteMen,
            givenBlackMen,
            givenWhiteKings,
            givenBlackKings,
        )
        given(piecesRepository.root).willReturn(givenId)
        given(piecesRepository[givenId]).willReturn(expectedPieces)

        // When
        val actualResult = classUnderTest.executeInBackground(Unit)

        // Then
        assertEquals(expectedPieces, actualResult)

    }

}
