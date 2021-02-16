package pl.org.seva.checkers.main

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import pl.org.seva.checkers.main.init.Bootstrap
import pl.org.seva.checkers.main.init.KodeinModuleBuilder
import pl.org.seva.checkers.main.init.instance

@Suppress("unused")
class MyApplication : Application() {

    init { Kodein.global.addImport(KodeinModuleBuilder(this).build()) }

    private val bootstrap by instance<Bootstrap>()

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch { bootstrap.boot(this@MyApplication) }
    }
}
