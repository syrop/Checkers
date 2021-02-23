package pl.org.seva.checkers.main.extension

inline fun <reified T> List<T>.copy() = ArrayList<T>(size).also { it.addAll(this) }
