package com.example.bugreport

import android.app.Application
import com.example.capturescreen.BugManager

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BugManager.init(this)
    }
}