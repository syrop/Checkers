package pl.org.seva.checkers.domain.cleanarchitecture.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.org.seva.checkers.domain.cleanarchitecture.exception.DomainException
import pl.org.seva.checkers.domain.cleanarchitecture.exception.UnknownDomainException

class UseCaseExecutorTest {

    private lateinit var classUnderTest: UseCaseExecutor

    @Before
    fun setUp() {
        classUnderTest = UseCaseExecutor()
    }

    @Test
    fun `Given successful useCase when execute then invokes onSuccess`() = runTest {
        // Given
        val useCase = object : UseCase<String, String> {
            override suspend fun execute(
                input: String,
                coroutineScope: CoroutineScope,
                onResult: (String) -> Unit,
            ) {
                onResult("Hello $input")
            }
        }
        var actualResult: String? = null

        // When
        val job = classUnderTest.execute(
            useCase = useCase,
            coroutineScope = this,
            value = "World",
            onSuccess = { actualResult = it },
        )
        job.join()

        // Then
        assertEquals("Hello World", actualResult)
    }

    @Test
    fun `Given useCase throwing runtime exception when execute then wraps in UnknownDomainException`() = runTest {
        // Given
        val cause = IllegalStateException("failure")
        val useCase = object : UseCase<Unit, Unit> {
            override suspend fun execute(
                input: Unit,
                coroutineScope: CoroutineScope,
                onResult: (Unit) -> Unit,
            ) {
                throw cause
            }
        }
        var actualException: DomainException? = null

        // When
        val job = classUnderTest.execute(
            useCase = useCase,
            coroutineScope = this,
            value = Unit,
            onException = { actualException = it },
        )
        job.join()

        // Then
        assertTrue(actualException is UnknownDomainException)
        assertEquals(cause, actualException?.cause)
    }
}
