package pl.org.seva.checkers.presentation.architecture

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import pl.org.seva.checkers.domain.cleanarchitecture.exception.DomainException
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCase

abstract class BasePresentation<VIEW_STATE : Any, NOTIFICATION : Any>(
    useCaseExecutorProvider: UseCaseExecutorProvider
) {

    var viewState = MutableStateFlow(this.initialState())

    protected abstract fun initialState(): VIEW_STATE

    private val useCaseExecutor by lazy {
        useCaseExecutorProvider()
    }

    protected fun <INPUT, OUTPUT> execute(
        useCase: UseCase<INPUT, OUTPUT>,
        coroutineScope: CoroutineScope,
        value: INPUT,
        onSuccess: (OUTPUT, CoroutineScope) -> Unit = { _, _ -> },
        onException: (DomainException) -> Unit = {}
    ) {
        useCaseExecutor.execute(useCase, coroutineScope, value, onSuccess, onException)
    }

    protected fun updateViewState(
        updatedState: VIEW_STATE.() -> VIEW_STATE
    ) = updateViewState(viewState.value.updatedState())

    protected fun updateViewState(newViewState: VIEW_STATE) {
        viewState.value = newViewState
    }

}
