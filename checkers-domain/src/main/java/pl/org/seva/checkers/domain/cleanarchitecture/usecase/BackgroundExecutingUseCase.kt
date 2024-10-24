package pl.org.seva.checkers.domain.cleanarchitecture.usecase

import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.CountDownLatch

abstract class BackgroundExecutingUseCase<REQUEST, RESULT> : UseCase<REQUEST, RESULT> {

    final override suspend fun execute(
        input: REQUEST,
        coroutineScope: CoroutineScope,
        onResult: (RESULT) -> Unit
    ) {
        val result = executeInBackground(input)
        onResult(result)
    }

    abstract suspend fun executeInBackground(
        request: REQUEST
    ): RESULT
}
