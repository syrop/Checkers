package pl.org.seva.myapplication.main.init

import android.content.Context
import android.os.Build
import androidx.test.espresso.idling.CountingIdlingResource
import org.kodein.di.Kodein
import org.kodein.di.KodeinProperty
import org.kodein.di.conf.global
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.multiton
import org.kodein.di.generic.singleton
import java.util.logging.Logger

inline fun <reified T : Any> instance(tag: Any? = null) = Kodein.global.instance<T>(tag)

inline fun <reified A, reified T : Any> instance(tag: Any? = null, arg: A) =
        Kodein.global.instance<A, T>(tag, arg = arg)

inline val <T> KodeinProperty<T>.value get() = provideDelegate(null, Build::ID).value

class KodeinModuleBuilder(private val ctx: Context) {

    fun build() = Kodein.Module("main") {
        bind<Bootstrap>() with singleton { Bootstrap() }
        bind<Logger>() with multiton { tag: String -> logger(tag) }
        bind<CountingIdlingResource>() with singleton { CountingIdlingResource("espresso") }
    }
}
