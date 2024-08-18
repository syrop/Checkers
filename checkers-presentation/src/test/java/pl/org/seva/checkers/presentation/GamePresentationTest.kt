package pl.org.seva.checkers.presentation

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCaseExecutor
import pl.org.seva.checkers.domain.usecase.BlackMoveUseCase
import pl.org.seva.checkers.domain.usecase.FetchPiecesUseCase
import pl.org.seva.checkers.domain.usecase.ResetUseCase
import pl.org.seva.checkers.domain.usecase.WhiteMoveUseCase
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.presentation.model.PiecesViewState

@RunWith(MockitoJUnitRunner::class)
class GamePresentationTest {

    private lateinit var classUnderTest: GamePresentation

    @Mock
    lateinit var useCaseExecutor: UseCaseExecutor

    @Mock
    lateinit var piecesDomainToPresentationMapper: PiecesDomainToPresentationMapper

    @Mock
    lateinit var piecesPresentationToDomainMapper: PiecesPresentationToDomainMapper

    @Mock
    lateinit var whiteMoveUseCase: WhiteMoveUseCase

    @Mock
    lateinit var blackMoveUseCase: BlackMoveUseCase

    @Mock
    lateinit var fetchPiecesUseCase: FetchPiecesUseCase

    @Mock
    lateinit var resetUseCase: ResetUseCase

    @Before
    fun setUp() {
        classUnderTest = GamePresentation(
            piecesDomainToPresentationMapper,
            piecesPresentationToDomainMapper,
            whiteMoveUseCase,
            blackMoveUseCase,
            fetchPiecesUseCase,
            resetUseCase) {
            useCaseExecutor
        }
    }

    @Test
    fun `Given no action when view state observed then returns default state`() {
        // Given
        val expectedViewState = PiecesViewState()

        // When
        val actualViewState = classUnderTest.viewState.value

        // Then
        assertEquals(expectedViewState, actualViewState)
    }

    @Test
    fun `Given no action when reset executes resetUseCase`() = runTest {

        // When
        classUnderTest.reset(this)

        // Then
        verify(useCaseExecutor).execute(
            eq(resetUseCase),
            eq(this),
            eq(Unit),
            any(),
            any(),
        )

    }

}
