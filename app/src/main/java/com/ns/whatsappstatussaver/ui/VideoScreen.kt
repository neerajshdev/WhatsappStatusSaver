package com.ns.whatsappstatussaver.ui

import android.app.Activity
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.ns.whatsappstatussaver.MainViewModel
import com.ns.whatsappstatussaver.loadInterstitialAd
import com.ns.whatsappstatussaver.ui.router.Screen
import com.ns.whatsappstatussaver.ui.router.ScreenType
import java.io.File


@Composable
fun VideoScreen(model: MainViewModel) {
    var context = LocalContext.current
    val uri = File(model.videoEntry!!.path).toURI()
    val mediaItem = MediaItem.fromUri(uri.toString())
    val exoPlayer = ExoPlayer.Builder(context).build()

    LaunchedEffect(context) {
        with(exoPlayer) {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    DisposableEffect(context, effect = {
        onDispose {
            exoPlayer.release()
        }
    })

    BackHandler {
        // go back to the home screen
        Screen.setScreen(ScreenType.HOME_SCREEN)
        loadInterstitialAd(context) {
            it.show(context as Activity)
        }
    }

    VideoPlayer(
        exoPlayer = exoPlayer
    )
}


@Composable
fun VideoPlayer(
    exoPlayer: ExoPlayer,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = {
                PlayerView(it)
            },
            update = { playerView ->
                with(playerView) {
                    layoutParams = ViewGroup.LayoutParams(-1, -1)
                    player = exoPlayer
                    useController = false
                }
            }
        )
    }
}