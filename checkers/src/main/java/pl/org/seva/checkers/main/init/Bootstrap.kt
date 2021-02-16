package pl.org.seva.checkers.main.init

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import pl.org.seva.checkers.main.createNotificationChannels


class Bootstrap {

    fun boot(ctx: Context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        createNotificationChannels(ctx)
    }
}
