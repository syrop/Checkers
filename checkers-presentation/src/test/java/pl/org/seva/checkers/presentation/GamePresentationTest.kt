package pl.org.seva.checkers.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCaseExecutor
import pl.org.seva.checkers.domain.model.PiecesDomainModel
import pl.org.seva.checkers.domain.usecase.BlackMoveUseCase
import pl.org.seva.checkers.domain.usecase.FetchPiecesUseCase
import pl.org.seva.checkers.domain.usecase.ResetUseCase
import pl.org.seva.checkers.domain.usecase.WhiteMoveUseCase
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.presentation.model.PiecesPresentationModel
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
            resetUseCase,
        ) {
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
    fun `When addWhite executes whiteMoveUseCase and executes blackMoveUseCase`() = runTest {
        // Given
        given(useCaseExecutor.execute(eq(whiteMoveUseCase), any(), any(), any(), any())).willCallRealMethod()
        given(whiteMoveUseCase.execute(any(), any(), any())).willCallRealMethod()
        given(whiteMoveUseCase.executeInBackground(any())).willReturn(
            PiecesDomainModel("id", "", emptyList(), emptyList(), emptyList(), emptyList())
        )
        given(piecesPresentationToDomainMapper.toDomain(any())).willReturn(
            PiecesDomainModel(
                "id",
                "",
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
            )
        )
        given(piecesDomainToPresentationMapper.toPresentation(any())).willReturn(
            PiecesPresentationModel(
                emptyList(),
                listOf(0 to 0),
                emptyList(),
                emptyList(),
            )
        )

        // When
        withContext(Dispatchers.IO) {
            classUnderTest.addWhite(1, 4, false, this)
        }

        // Then
        verify(useCaseExecutor).execute(
            eq(whiteMoveUseCase),
            any(),
            any(),
            any(),
            any(),
        )

        verify(useCaseExecutor).execute(
            eq(blackMoveUseCase),
            any(),
            any(),
            any(),
            any(),
        )
    }

    @Test
    fun `Given no remaining black pieces when addWhite completes then sets whiteWon true without executing blackMoveUseCase`() = runTest {
        // Given
        given(useCaseExecutor.execute(eq(whiteMoveUseCase), any(), any(), any(), any())).willCallRealMethod()
        given(whiteMoveUseCase.execute(any(), any(), any())).willCallRealMethod()
        given(whiteMoveUseCase.executeInBackground(any())).willReturn(
            PiecesDomainModel("id", "", listOf(1 to 0), emptyList(), emptyList(), emptyList())
        )
        given(piecesPresentationToDomainMapper.toDomain(any())).willReturn(
            PiecesDomainModel("id", "", listOf(1 to 0), emptyList(), emptyList(), emptyList())
        )
        given(piecesDomainToPresentationMapper.toPresentation(any())).willReturn(
            PiecesPresentationModel(
                whiteMen = emptyList(),
                blackMen = emptyList(),
                whiteKings = listOf(1 to 0),
                blackKings = emptyList(),
            )
        )

        // When
        withContext(Dispatchers.IO) {
            classUnderTest.addWhite(1, 0, false, this)
        }

        // Then
        assertTrue(classUnderTest.viewState.value.whiteWon)
        assertEquals(listOf(1 to 0), classUnderTest.viewState.value.pieces.whiteKings.toList())
        verify(useCaseExecutor, never()).execute(
            eq(blackMoveUseCase),
            any(),
            any(),
            any(),
            any(),
        )
    }

    @Test
    fun `Given pieces when storeState and removeWhite and restoreState then restores original state`() = runTest {
        // Given
        classUnderTest.addWhite(2, 5, false, this)
        classUnderTest.storeState()

        // When
        val removed = classUnderTest.removeWhite(2, 5)
        assertTrue(removed)
        assertTrue(classUnderTest.viewState.value.pieces.whiteMen.none())
        classUnderTest.restoreState()

        // Then
        assertEquals(setOf(2 to 5), classUnderTest.viewState.value.pieces.whiteMen.toSet())
        assertFalse(classUnderTest.removeBlack(7 to 7))
    }

    @Test
    fun `When fetchPieces executes fetchPiecesUseCase`() = runTest {
        // When
        classUnderTest.fetchPieces(this)

        // Then
        verify(useCaseExecutor).execute(
            eq(fetchPiecesUseCase),
            eq(this),
            eq(Unit),
            any(),
            any(),
        )
    }

    @Test
    fun `When reset executes resetUseCase without redundant fetchPiecesUseCase`() = runTest {
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
        verify(useCaseExecutor, never()).execute(
            eq(fetchPiecesUseCase),
            any(),
            any(),
            any(),
            any(),
        )
    }

}
