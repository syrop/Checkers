package pl.org.seva.checkers.presentation.architecture

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.org.seva.checkers.domain.cleanarchitecture.exception.DomainException
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCase
import pl.org.seva.checkers.presentation.viewmodel.livedata.SingleLiveEvent
import pl.org.seva.checkers.presentation.viewmodel.usecase.UseCaseExecutorProvider

abstract class BaseViewModel<VIEW_STATE : Any, NOTIFICATION : Any>(
    useCaseExecutorProvider: UseCaseExecutorProvider
) : ViewModel() {

    private var _viewState by mutableStateOf(this.initialState())

    val viewState: VIEW_STATE = _viewState

    private val _notification = SingleLiveEvent<NOTIFICATION>()
    val notification = _notification.asLiveData()

    protected abstract fun initialState(): VIEW_STATE

    private val useCaseExecutor by lazy {
        useCaseExecutorProvider(viewModelScope)
    }

    protected fun <OUTPUT> execute(
        useCase: UseCase<Unit, OUTPUT>,
        onSuccess: (OUTPUT) -> Unit = {},
        onException: (DomainException) -> Unit = {}
    ) {
        execute(useCase, Unit, onSuccess, onException)
    }

    protected fun <INPUT, OUTPUT> execute(
        useCase: UseCase<INPUT, OUTPUT>,
        value: INPUT,
        onSuccess: (OUTPUT) -> Unit = {},
        onException: (DomainException) -> Unit = {}
    ) {
        useCaseExecutor.execute(useCase, value, onSuccess, onException)
    }

    protected fun updateViewState(newViewState: VIEW_STATE) {
        _viewState = newViewState
    }

    protected fun updateViewState(
        updatedState: VIEW_STATE.() -> VIEW_STATE
    ) = updateViewState(viewState.updatedState())

    protected fun notify(notification: NOTIFICATION) {
        _notification.value = notification
    }

    private fun <T> LiveData<T>.asLiveData() = this
}
