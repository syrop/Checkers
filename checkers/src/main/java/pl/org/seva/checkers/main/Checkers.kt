package pl.org.seva.checkers.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

@Suppress("unused")
class Checkers : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
