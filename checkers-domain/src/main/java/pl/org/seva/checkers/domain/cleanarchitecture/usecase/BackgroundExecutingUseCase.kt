package pl.org.seva.checkers.domain.cleanarchitecture.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BackgroundExecutingUseCase<REQUEST, RESULT> : UseCase<REQUEST, RESULT> {
    final override suspend fun execute(
        input: REQUEST,
        onResult: (RESULT) -> Unit
    ) {
        val result = withContext(Dispatchers.Default) {
            executeInBackground(input)
        }
        onResult(result)
    }

    abstract fun executeInBackground(
        request: REQUEST
    ): RESULT
}
