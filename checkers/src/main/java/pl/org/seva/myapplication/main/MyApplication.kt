package pl.org.seva.myapplication.main

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import pl.org.seva.myapplication.main.init.Bootstrap
import pl.org.seva.myapplication.main.init.KodeinModuleBuilder
import pl.org.seva.myapplication.main.init.instance

@Suppress("unused")
class MyApplication : Application() {

    init { Kodein.global.addImport(KodeinModuleBuilder(this).build()) }

    private val bootstrap by instance<Bootstrap>()

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch { bootstrap.boot(this@MyApplication) }
    }
}
