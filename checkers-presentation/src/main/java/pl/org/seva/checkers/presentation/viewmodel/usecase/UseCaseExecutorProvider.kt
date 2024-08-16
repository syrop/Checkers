package pl.org.seva.checkers.presentation.viewmodel.usecase

import kotlinx.coroutines.CoroutineScope
import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCaseExecutor

typealias UseCaseExecutorProvider =
    @JvmSuppressWildcards () -> UseCaseExecutor
