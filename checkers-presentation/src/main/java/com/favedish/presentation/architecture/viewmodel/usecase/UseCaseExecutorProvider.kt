package com.favedish.presentation.architecture.viewmodel.usecase

import pl.org.seva.checkers.domain.cleanarchitecture.usecase.UseCaseExecutor
import kotlinx.coroutines.CoroutineScope

typealias UseCaseExecutorProvider =
    @JvmSuppressWildcards (coroutineScope: CoroutineScope) -> UseCaseExecutor
