package com.nibodev.statussaver.ui

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.nibodev.statussaver.AppOpenAdManager
import com.nibodev.statussaver.MainViewModel
import com.nibodev.statussaver.appOpenAd
import com.nibodev.statussaver.ui.screen.LangScreen
import com.nibodev.statussaver.ui.screen.LoadingScreen

/*
nativeAdId = "ca-app-pub-3940256099942544/2247696110",
appOpenAdId = "ca-app-pub-3940256099942544/3419835294",
interstitialAdId = "ca-app-pub-3940256099942544/1033173712"
*/

val appOpenAdManager = AppOpenAdManager("ca-app-pub-3940256099942544/3419835294")

@Composable
fun MainUI(model: MainViewModel) {
    // navigation controller
    val nc = LocalNavController.current
    val context = LocalContext.current

    fun landToLanguagePage() {
        nc.replace {
            LangScreen()
        }
    }

    Surface(color = MaterialTheme.colors.background, modifier = Modifier) {
        Column {
            Navigator(controller = nc) {
                LoadingScreen()
                LaunchedEffect(Unit) {
                    appOpenAd(
                        context as Activity,
                        appOpenAdManager,
                        onAdDismissedFsc = {
                            landToLanguagePage()
                        }
                    )
                }
            }
        }
    }
    
    
//    // test native ad
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        NativeAdUnit(nativeAdManager = nativeAdManager)
//    }
}