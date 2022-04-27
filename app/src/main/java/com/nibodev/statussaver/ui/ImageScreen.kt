package com.nibodev.statussaver.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.nibodev.statussaver.MainViewModel
import com.nibodev.statussaver.loadInterstitialAd
import com.nibodev.statussaver.ui.router.Screen
import com.nibodev.statussaver.ui.router.ScreenType

@Composable
fun ImageScreen(model: MainViewModel) {
    val ctx = LocalContext.current as Activity
    Image(
        contentScale = ContentScale.FillWidth,
        bitmap = model.imageEntry!!.image , contentDescription = null,
        modifier = Modifier.fillMaxSize().background(color = Color.Black)
    )
    BackHandler {
        Screen.setScreen(ScreenType.HOME_SCREEN)
        loadInterstitialAd(ctx) {it.show(ctx)}
    }
}