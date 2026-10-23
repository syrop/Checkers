package pl.org.seva.checkers.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
import java.util.UUID

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
            blackKings,
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

    @Test
    fun `Given no matching id when get then throws exception`() {
        // Given
        val expectedId = UUID.randomUUID().toString()
        val expectedException = IllegalArgumentException("wrong Id: $expectedId")

        // When
        val actualException = try {
            classUnderTest[expectedId]
            null
        } catch (e: IllegalArgumentException) {
            e
        }

        // Then
        @Suppress("KotlinConstantConditions")
        assertTrue(actualException is IllegalArgumentException)
        @Suppress("KotlinConstantConditions")
        assertEquals(expectedException.message, actualException?.message)
    }

    @Test
    fun `Given tree when prune then keeps chosen branch as new root and deletes siblings`() {
        // Given
        val realDataToDomain = PiecesDataToDomainMapper()
        val realDomainToData = PiecesDomainToDataMapper()
        val rootData = PiecesDataModel("root", "", listOf(0 to 5), listOf(1 to 2), emptyList(), emptyList(), null)
        given(piecesDatasource.load()).willReturn(setOf(rootData))
        val repository = PiecesLiveRepository(piecesDatasource, realDataToDomain, realDomainToData)

        val childA = PiecesDomainModel("childA", "root", listOf(0 to 5), listOf(0 to 3), emptyList(), emptyList(), level = 1)
        val childB = PiecesDomainModel("childB", "root", listOf(0 to 5), listOf(2 to 3), emptyList(), emptyList(), level = 1)
        val grandChildA = PiecesDomainModel("grandChildA", "childA", listOf(1 to 4), listOf(0 to 3), emptyList(), emptyList(), level = 2)

        repository.addLeaf(childA)
        repository.addLeaf(childB)
        repository.addLeaf(grandChildA)

        // When
        repository.prune("childA")

        // Then
        assertEquals("childA", repository.root)
        assertEquals(0, repository["childA"].level)
        assertEquals("", repository["childA"].parent)
        assertEquals(1, repository["grandChildA"].level)
        assertEquals(setOf("grandChildA"), repository.getLeaves().toSet())
    }

    @Test
    fun `Given populated repository when reset then clears old leaves and restores initial root`() {
        // Given
        val realDataToDomain = PiecesDataToDomainMapper()
        val realDomainToData = PiecesDomainToDataMapper()
        val rootData = PiecesDataModel("root", "", listOf(0 to 5), listOf(1 to 2), emptyList(), emptyList(), null)
        given(piecesDatasource.load()).willReturn(setOf(rootData))
        val repository = PiecesLiveRepository(piecesDatasource, realDataToDomain, realDomainToData)
        val child = PiecesDomainModel("child", "root", listOf(1 to 4), listOf(1 to 2), emptyList(), emptyList(), level = 1)
        repository.addLeaf(child)

        // When
        repository.reset()

        // Then
        assertEquals("root", repository.root)
        assertEquals(setOf("root"), repository.getLeaves().toSet())
        assertTrue(repository.getImmediateMoves().none())
    }

    @Test
    fun `Given level 1 children when find and getImmediateMoves then returns matching child`() {
        // Given
        val realDataToDomain = PiecesDataToDomainMapper()
        val realDomainToData = PiecesDomainToDataMapper()
        val rootData = PiecesDataModel("root", "", listOf(0 to 5), listOf(1 to 2), emptyList(), emptyList(), null)
        given(piecesDatasource.load()).willReturn(setOf(rootData))
        val repository = PiecesLiveRepository(piecesDatasource, realDataToDomain, realDomainToData)

        val childA = PiecesDomainModel("childA", "root", listOf(1 to 4, 2 to 5), listOf(1 to 2), emptyList(), emptyList(), level = 1)
        repository.addLeaf(childA)

        val soughtReordered = PiecesDomainModel("sought", "", listOf(2 to 5, 1 to 4), listOf(1 to 2), emptyList(), emptyList())

        // When
        val foundId = repository.find(soughtReordered)
        val immediateMoves = repository.getImmediateMoves().toList()

        // Then
        assertEquals("childA", foundId)
        assertEquals(listOf(childA), immediateMoves)
    }

    @Test
    fun `Given new state when updateState then replaces root and leaves`() {
        // Given
        val realDataToDomain = PiecesDataToDomainMapper()
        val realDomainToData = PiecesDomainToDataMapper()
        val rootData = PiecesDataModel("root", "", listOf(0 to 5), listOf(1 to 2), emptyList(), emptyList(), null)
        given(piecesDatasource.load()).willReturn(setOf(rootData))
        val repository = PiecesLiveRepository(piecesDatasource, realDataToDomain, realDomainToData)

        val newState = PiecesDomainModel("newRoot", "oldParent", listOf(3 to 4), listOf(1 to 2), emptyList(), emptyList(), level = 3)

        // When
        repository.updateState(newState)

        // Then
        assertEquals("newRoot", repository.root)
        assertEquals(0, repository["newRoot"].level)
        assertEquals(setOf("newRoot"), repository.getLeaves(0).toSet())
    }

}
