package pl.org.seva.checkers.presentation

import com.sun.net.httpserver.Authenticator.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
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
import java.util.concurrent.CountDownLatch

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
    fun `When addWhite executes whiteMoveUseCase and executes blackMoveUseCase`() = runTest {

        // Given
        given(useCaseExecutor.execute(eq(whiteMoveUseCase), any(), any(), any(), any())).willCallRealMethod()
        given(whiteMoveUseCase.execute(any(), any(), any())).willCallRealMethod()
        given(whiteMoveUseCase.executeInBackground(any())).willReturn(PiecesDomainModel("id", "", emptyList(), emptyList(), emptyList(), emptyList()))
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
                emptyList()
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
    fun `When reset executes resetUseCase`() = runTest {

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
