package ru.netology.kotlin_for_android_hw_1.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.kotlin_for_android_hw_1.auth.AppAuthorization

@HiltAndroidApp
class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        AppAuthorization.init(this)
    }
}