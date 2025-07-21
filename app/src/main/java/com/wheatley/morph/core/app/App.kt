package com.wheatley.morph.core.app

import android.app.Application
import com.wheatley.morph.di.initKoinModules

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinModules(this)
    }
}