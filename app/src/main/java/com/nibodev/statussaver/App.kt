package com.nibodev.statussaver

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        Firebase.remoteConfig.setConfigSettingsAsync(configSettings)
        MobileAds.initialize(this) {}
    }
}