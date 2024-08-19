package pl.org.seva.checkers.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.MethodRule
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.junit.MockitoJUnit
import pl.org.seva.checkers.data.model.PiecesDataModel
import pl.org.seva.checkers.domain.model.PiecesDomainModel

@RunWith(Parameterized::class)
class PiecesDataToDomainMapperTest(
    private val givenInput: PiecesDataModel,
    private val expected: PiecesDomainModel,
) {

    companion object {

        private const val ID = "id"
        private const val PARENT_ID = ""
        private val WHITE_MEN = listOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        private val BLACK_MEN = listOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
        private val WHITE_KINGS = emptyList<Pair<Int, Int>>()
        private val BLACK_KINGS = emptyList<Pair<Int, Int>>()

        @JvmStatic
        @Parameters(name = "Given {0} then returns {1}")
        fun data() = listOf(
            testCase(
                ID,
                PARENT_ID,
                WHITE_MEN,
                BLACK_MEN,
                WHITE_KINGS,
                BLACK_KINGS,
            ),
        )

        @Suppress("SameParameterValue")
        private fun testCase(
            id: String,
            parentId: String,
            whiteMen: List<Pair<Int, Int>>,
            blackMen: List<Pair<Int, Int>>,
            whiteKings: List<Pair<Int, Int>>,
            blackKings: List<Pair<Int, Int>>,
        ) = arrayOf(
            PiecesDataModel(
                id,
                parentId,
                whiteMen,
                blackMen,
                blackKings,
                whiteKings,
                0,
            ),
            PiecesDomainModel(
                id,
                parentId,
                whiteMen,
                blackMen,
                whiteKings,
                blackKings,
                heuristics = 0,
        ),
        )
    }

    @get:Rule
    val mockitoRule: MethodRule = MockitoJUnit.rule()

    private lateinit var classUnderTest: PiecesDataToDomainMapper

    @Before
    fun setUp() {
        classUnderTest = PiecesDataToDomainMapper()
    }

    @Test
    fun `When toPresentation`() {

        // When
        val actualValue = classUnderTest.toDomain(givenInput)

        // Then
        assertEquals(expected, actualValue)
    }

}
