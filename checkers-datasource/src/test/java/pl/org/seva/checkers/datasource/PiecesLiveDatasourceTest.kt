package pl.org.seva.checkers.datasource

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.given
import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.datasource.mapper.PiecesMemoryToDataMapper
import pl.org.seva.checkers.datasource.model.PiecesMemoryModel

@RunWith(MockitoJUnitRunner::class)
class PiecesLiveDatasourceTest {

    @Mock
    private lateinit var piecesMemoryToDataMapper: PiecesMemoryToDataMapper

    private lateinit var classUnderTest: PiecesLiveDatasource

    private val memoryModel = PiecesMemoryModel(
        id = "root",
        parent = "",
        whiteMen = listOf(0 to 7),
        blackMen = listOf(1 to 0),
        whiteKings = emptyList(),
        blackKings = emptyList(),
    )

    private val dataModel = PiecesDataModel(
        id = "root",
        parent = "",
        whiteMen = listOf(0 to 7),
        blackMen = listOf(1 to 0),
        whiteKings = emptyList(),
        blackKings = emptyList(),
        heuristics = null,
    )

    @Before
    fun setUp() {
        classUnderTest = PiecesLiveDatasource(
            piecesStore = listOf(memoryModel),
            piecesMemoryToDataMapper = piecesMemoryToDataMapper,
        )
    }

    @Test
    fun `Given memory models when load then returns mapped data models`() {
        // Given
        given(piecesMemoryToDataMapper.toData(memoryModel)).willReturn(dataModel)

        // When
        val actualResult = classUnderTest.load()

        // Then
        assertEquals(listOf(dataModel), actualResult)
    }
}
