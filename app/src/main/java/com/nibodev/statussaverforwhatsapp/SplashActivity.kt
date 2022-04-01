package com.nibodev.statussaverforwhatsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.nibodev.statussaverforwhatsapp.ui.components.SplashContent

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashContent()
        }
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        fetchAdConfig {}
        MobileAds.initialize(this) {}


//        // for testing ads
//        isDebug {
//            val requestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(
//                listOf(
//                    "810358a3-ad4b-4d28-b61f-4d2e681a0349", // pixel emulator api 30
//                    "4e13226e-34b8-4bdd-be57-c01ad06123c0" // redmi note 4
//                )
//            ).build()
//            MobileAds.setRequestConfiguration(requestConfiguration)
//        }


        val handler = Handler(mainLooper)
        handler.postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }
}



