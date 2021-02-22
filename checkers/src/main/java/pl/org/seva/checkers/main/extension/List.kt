package pl.org.seva.checkers.main.extension

inline fun <reified T> List<T>.copy() = listOf(*this.toTypedArray())
