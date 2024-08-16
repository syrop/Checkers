package pl.org.seva.checkers.domain.cleanarchitecture.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.org.seva.checkers.domain.cleanarchitecture.exception.DomainException
import pl.org.seva.checkers.domain.cleanarchitecture.exception.UnknownDomainException
import kotlin.coroutines.cancellation.CancellationException

class UseCaseExecutor {

    fun <INPUT, OUTPUT> execute(
        useCase: UseCase<INPUT, OUTPUT>,
        coroutineScope: CoroutineScope,
        value: INPUT,
        onSuccess: (OUTPUT, CoroutineScope) -> Unit = { _, _ ->},
        onException: (DomainException) -> Unit = {}
    ) {
        coroutineScope.launch {
            try {
                useCase.execute(value, coroutineScope, onSuccess)
            }
            catch (ignore: CancellationException) {
            }
            catch (throwable: Throwable) {
                onException(
                    (throwable as? DomainException)
                        ?: UnknownDomainException(throwable)
                )
            }
        }
    }

}
