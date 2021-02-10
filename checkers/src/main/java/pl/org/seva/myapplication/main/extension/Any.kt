package pl.org.seva.myapplication.main.extension

import pl.org.seva.myapplication.main.init.instance
import java.util.logging.Logger

val Any.log get() = instance<String, Logger>(arg = this::class.java.name)