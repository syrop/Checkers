package pl.org.seva.myapplication.main.init

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

suspend fun <T> Channel<T>.onEach(block: suspend (T) -> Unit) {
    while (true) {
        block(receive())
    }
}

fun <T> Channel<T>.onEach(scope: CoroutineScope, block: suspend (T) -> Unit) {
    scope.launch {
        onEach(block)
    }
}
