package pl.org.seva.checkers.domain.cleanarchitecture.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.org.seva.checkers.domain.cleanarchitecture.exception.DomainException
import pl.org.seva.checkers.domain.cleanarchitecture.exception.UnknownDomainException
import kotlin.coroutines.cancellation.CancellationException

class UseCaseExecutor {

    fun <INPUT, OUTPUT> execute(
        useCase: UseCase<INPUT, OUTPUT>,
        coroutineScope: CoroutineScope,
        value: INPUT,
        onSuccess: (OUTPUT) -> Unit = {},
        onException: (DomainException) -> Unit = {},
    ): Job = coroutineScope.launch {
        try {
            useCase.execute(value, coroutineScope, onSuccess)
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            onException(
                (throwable as? DomainException)
                    ?: UnknownDomainException(throwable)
            )
        }
    }

}
