package pl.org.seva.checkers.main.init

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate


class Bootstrap {

    fun boot(ctx: Context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
