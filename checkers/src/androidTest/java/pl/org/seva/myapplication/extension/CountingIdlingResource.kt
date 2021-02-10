package pl.org.seva.myapplication.extension

import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@ExperimentalCoroutinesApi
suspend fun CountingIdlingResource.await() = suspendCancellableCoroutine<Unit> { cont ->
    registerIdleTransitionCallback {
        cont.resume(Unit)
    }
}
