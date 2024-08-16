package pl.org.seva.checkers.presentation.architecture

import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCaseExecutor

typealias UseCaseExecutorProvider =
    @JvmSuppressWildcards () -> UseCaseExecutor
