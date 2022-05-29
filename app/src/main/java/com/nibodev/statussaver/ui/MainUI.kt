package com.nibodev.statussaver.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.nibodev.statussaver.*
import com.nibodev.statussaver.ui.screen.HomePage
import com.nibodev.statussaver.ui.screen.LangPage
import com.nibodev.statussaver.ui.screen.LoadingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect

/*
nativeAdId = "ca-app-pub-3940256099942544/2247696110",
appOpenAdId = "ca-app-pub-3940256099942544/3419835294",
interstitialAdId = "ca-app-pub-3940256099942544/1033173712"
*/



@Composable
fun MainUI(model: MainViewModel) {
    // navigation controller
    val navController = LocalNavController.current

    Column {
        Navigator(controller = navController) {
            LoadingScreen()
        }
    }
}