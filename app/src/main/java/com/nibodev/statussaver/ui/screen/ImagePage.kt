package com.nibodev.statussaver.ui.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.nibodev.statussaver.console
import com.nibodev.statussaver.interstitialAd
import com.nibodev.statussaver.models.ImageMedia
import com.nibodev.statussaver.navigation.LocalNavController
import com.nibodev.statussaver.ui.interAdCounter
import com.nibodev.statussaver.ui.interstitialAdManager

@Composable
fun ImagePage(image: ImageMedia) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    image.Draw(Modifier.fillMaxSize())

    Box {
        BackHandler {
            console("showing ad")
            interstitialAd(
                activity = context as Activity,
                interstitialAdManager = interstitialAdManager,
                interAdCounter = interAdCounter,
                doLast = {
                    navController.pop()
                }
            )
        }
    }

}