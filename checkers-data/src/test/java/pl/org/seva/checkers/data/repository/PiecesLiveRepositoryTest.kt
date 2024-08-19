package pl.org.seva.checkers.data.repository

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.given
import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.data.mapper.PiecesDataToDomainMapper
import pl.org.seva.checkers.data.mapper.PiecesDomainToDataMapper
import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.domain.model.PiecesDomainModel

@RunWith(MockitoJUnitRunner::class)
class PiecesLiveRepositoryTest {

    private lateinit var classUnderTest: PiecesLiveRepository

    @Mock
    lateinit var piecesDatasource: PiecesDatasource

    @Mock
    lateinit var dataToDomainMapper: PiecesDataToDomainMapper

    @Mock
    lateinit var domainToDataMapper: PiecesDomainToDataMapper

    @Before
    fun setUp() {
        classUnderTest = PiecesLiveRepository(
            piecesDatasource,
            dataToDomainMapper,
            domainToDataMapper,
        )
    }

    @Test
    fun `given pieces when get returns pieces`() {
        // Given
        val id = "id"
        val parentId = ""
        val whiteMen = listOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        val blackMen = listOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
        val whiteKings = emptyList<Pair<Int, Int>>()
        val blackKings = emptyList<Pair<Int, Int>>()
        val expected = PiecesDomainModel(
            id,
            parentId,
            whiteMen,
            blackMen,
            whiteKings,
            blackKings,
        )
        val piecesData = PiecesDataModel(
            id,
            parentId,
            whiteMen,
            blackMen,
            whiteKings,
            whiteMen,
            null,
        )
        given(piecesDatasource.load()).willReturn(setOf(piecesData))
        given(dataToDomainMapper.toDomain(piecesData)).willReturn(expected)
        classUnderTest.reset()

        // When
        val actualValue = classUnderTest[classUnderTest.root]

        // Then
        assertEquals(expected, actualValue)
    }

}
