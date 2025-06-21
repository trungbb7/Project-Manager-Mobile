package com.example.projectmanagerapp

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import com.example.projectmanagerapp.BuildConfig

@HiltAndroidApp
class ProjectManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if(!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

    }
}
