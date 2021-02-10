package pl.org.seva.myapplication.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay

class VM : ViewModel() {

    val ld = liveData(viewModelScope.coroutineContext, Long.MAX_VALUE) {
        try {
            delay(Long.MAX_VALUE)
            emit(0)
        }
        finally {
            println("wiktor end of the liveData")
        }
    }
}
