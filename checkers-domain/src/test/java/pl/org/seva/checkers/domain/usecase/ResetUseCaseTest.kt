package pl.org.seva.checkers.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.given
import org.mockito.kotlin.inOrder
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

@RunWith(MockitoJUnitRunner::class)
class ResetUseCaseTest {

    @Mock
    private lateinit var piecesRepository: PiecesRepository

    private lateinit var classUnderTest: ResetUseCase

    @Before
    fun setUp() {
        classUnderTest = ResetUseCase(piecesRepository)
    }

    @Test
    fun `When executeInBackground then resets repository and returns root pieces`() = runTest {
        // Given
        val expectedPieces = PiecesDomainModel(
            id = "root",
            parent = "",
            whiteMen = listOf(0 to 7),
            blackMen = listOf(1 to 0),
            whiteKings = emptyList(),
            blackKings = emptyList(),
        )
        given(piecesRepository.root).willReturn("root")
        given(piecesRepository["root"]).willReturn(expectedPieces)

        // When
        val actualResult = classUnderTest.executeInBackground(Unit)

        // Then
        inOrder(piecesRepository) {
            verify(piecesRepository).reset()
            verify(piecesRepository).root
            verify(piecesRepository)["root"]
        }
        assertEquals(expectedPieces, actualResult)
    }
}
