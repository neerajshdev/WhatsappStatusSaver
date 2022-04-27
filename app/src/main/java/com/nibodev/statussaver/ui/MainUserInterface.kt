package com.nibodev.statussaver.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.nibodev.statussaver.MainViewModel
import com.nibodev.statussaver.loadInterstitialAd
import com.nibodev.statussaver.shouldReqAd
import com.nibodev.statussaver.ui.components.BannerAdUnit
import com.nibodev.statussaver.ui.router.Screen
import com.nibodev.statussaver.ui.router.ScreenType

@Composable
fun MainUserInterface(model: MainViewModel) {
    val ctx = LocalContext.current as Activity

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier) {
            Crossfade(targetState = Screen.current.value) {
                when (it) {
                    ScreenType.HOME_SCREEN -> HomeScreen(model)
                    ScreenType.SCREEN_VIDEO -> VideoScreen(model)
                    ScreenType.SCREEN_IMAGE -> ImageScreen(model)
                }
            }
        }

        if (Screen.current.value == ScreenType.HOME_SCREEN && shouldReqAd)
            BannerAdUnit(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
    }


    var enable by remember {
        mutableStateOf(true)
    }
    BackHandler(enabled = enable) {
        loadInterstitialAd(ctx){it.show(ctx)}
        enable = false
    }
}