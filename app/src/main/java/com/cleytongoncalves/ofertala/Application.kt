package com.cleytongoncalves.ofertala

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.singhajit.sherlock.core.Sherlock
import timber.log.Timber

var LOGGED_USER_ID = "asT8x573w454hqeu1Peq"
class Application : MultiDexApplication() {
    
    companion object {
        operator fun get(context: Context): Application {
            return context.applicationContext as Application
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Sherlock.init(this)
        }
    }
}