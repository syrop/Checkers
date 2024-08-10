package pl.org.seva.checkers.domain.cleanarchitecture.exception

import pl.org.seva.checkers.domain.cleanarchitecture.exception.DomainException

class UnknownDomainException(throwable: Throwable) : DomainException(throwable) {
    constructor(errorMessage: String) : this(Throwable(errorMessage))
}
