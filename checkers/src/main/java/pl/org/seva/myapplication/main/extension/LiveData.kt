package pl.org.seva.myapplication.main.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe

operator fun <T> LiveData<T>.invoke(owner: LifecycleOwner, observer: (T) -> Unit) =
        observe(owner) { observer(it) }

operator fun <T> LiveData<T>.plus(owner: LifecycleOwner): ((T) -> Unit) -> Unit = { invoke(owner, it) }
