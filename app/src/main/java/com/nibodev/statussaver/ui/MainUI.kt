package com.nibodev.statussaver.ui

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.nibodev.statussaver.AppOpenAdManager
import com.nibodev.statussaver.MainViewModel
import com.nibodev.statussaver.appOpenAd
import com.nibodev.statussaver.isNetworkConnected
import com.nibodev.statussaver.ui.screen.LangPage
import com.nibodev.statussaver.ui.screen.LoadingScreen

/*
nativeAdId = "ca-app-pub-3940256099942544/2247696110",
appOpenAdId = "ca-app-pub-3940256099942544/3419835294",
interstitialAdId = "ca-app-pub-3940256099942544/1033173712"
*/

val appOpenAdManager = AppOpenAdManager("ca-app-pub-3940256099942544/3419835294", 1)

@Composable
fun MainUI(model: MainViewModel) {
    // navigation controller
    val navController = LocalNavController.current
    val context = LocalContext.current

    fun landToLanguagePage() {
        navController.push {
            LangPage()
        }
    }
    Column {
        Navigator(controller = navController) {
            LoadingScreen()
            LaunchedEffect(Unit) {
                if(isNetworkConnected(context)) {
                    appOpenAd(
                        context as Activity,
                        appOpenAdManager,
                        onAdDismissedFsc = {
                            landToLanguagePage()
                        }
                    )
                } else {
                    landToLanguagePage()
                }
            }
        }
    }
}