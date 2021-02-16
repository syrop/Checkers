package pl.org.seva.checkers.main.extension

import pl.org.seva.checkers.main.init.instance
import java.util.logging.Logger

val Any.log get() = instance<String, Logger>(arg = this::class.java.name)