package pl.org.seva.checkers.presentation.architecture

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.org.seva.checkers.domain.cleanarchitecture.exception.DomainException
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCase

abstract class BasePresentation<VIEW_STATE>(
    useCaseExecutorProvider: UseCaseExecutorProvider,
) {

    protected abstract val initialViewState: VIEW_STATE

    private val mutableViewState by lazy { MutableStateFlow(initialViewState) }
    val viewState: StateFlow<VIEW_STATE> by lazy { mutableViewState.asStateFlow() }

    private val useCaseExecutor by lazy {
        useCaseExecutorProvider()
    }

    @Suppress("RedundantNullableReturnType")
    protected fun <INPUT, OUTPUT> execute(
        useCase: UseCase<INPUT, OUTPUT>,
        coroutineScope: CoroutineScope,
        value: INPUT,
        onSuccess: (OUTPUT) -> Unit = {},
        onException: (DomainException) -> Unit = {},
    ): Job? = useCaseExecutor.execute(useCase, coroutineScope, value, onSuccess, onException)

    protected fun updateViewState(
        updatedState: VIEW_STATE.() -> VIEW_STATE,
    ) = updateViewState(viewState.value.updatedState())

    protected fun updateViewState(newViewState: VIEW_STATE) {
        mutableViewState.value = newViewState
    }

}
