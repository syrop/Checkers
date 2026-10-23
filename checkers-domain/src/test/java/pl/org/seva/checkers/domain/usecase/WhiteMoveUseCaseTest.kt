package pl.org.seva.checkers.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.repository.PiecesRepository

@RunWith(MockitoJUnitRunner::class)
class WhiteMoveUseCaseTest {

    @Mock
    private lateinit var piecesRepository: PiecesRepository

    private lateinit var classUnderTest: WhiteMoveUseCase

    private val requestPieces = PiecesDomainModel(
        id = "move-1",
        parent = "",
        whiteMen = listOf(1 to 4),
        blackMen = listOf(0 to 1),
        whiteKings = emptyList(),
        blackKings = emptyList(),
    )

    @Before
    fun setUp() {
        classUnderTest = WhiteMoveUseCase(piecesRepository)
    }

    @Test
    fun `Given matching state in repository when executeInBackground then prunes to found id`() = runTest {
        // Given
        given(piecesRepository.find(requestPieces)).willReturn("existing-node-id")

        // When
        val actualResult = classUnderTest.executeInBackground(requestPieces)

        // Then
        verify(piecesRepository).prune("existing-node-id")
        verify(piecesRepository, never()).updateState(any())
        assertEquals(requestPieces, actualResult)
    }

    @Test
    fun `Given no matching state in repository when executeInBackground then updates state`() = runTest {
        // Given
        given(piecesRepository.find(requestPieces)).willReturn("")

        // When
        val actualResult = classUnderTest.executeInBackground(requestPieces)

        // Then
        verify(piecesRepository).updateState(requestPieces)
        verify(piecesRepository, never()).prune(any())
        assertEquals(requestPieces, actualResult)
    }
}
