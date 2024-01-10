package com.bestteam.myfitroutine

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyFitRoutineApplication : Application(){
    companion object {
        private lateinit var application: MyFitRoutineApplication

        fun getInstance(): MyFitRoutineApplication = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}