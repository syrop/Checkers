package pl.org.seva.checkers.main.init

import pl.org.seva.checkers.BuildConfig
import java.util.logging.Logger

fun logger(tag: String) =
        LoggerBuilder().apply {
            this.tag = tag
        }.build()

class LoggerBuilder {

    var tag: String = ""

    fun build() = Logger.getLogger(tag)!!.apply {
        if (!BuildConfig.DEBUG) {
            @Suppress("UsePropertyAccessSyntax")
            setFilter { false }
        }
    }
}
