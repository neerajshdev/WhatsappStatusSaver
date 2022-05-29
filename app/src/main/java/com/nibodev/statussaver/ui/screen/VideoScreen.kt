package com.nibodev.statussaver.ui.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.nibodev.statussaver.interstitialAd
import com.nibodev.statussaver.models.StatusVideo
import com.nibodev.statussaver.ui.LocalNavController
import com.nibodev.statussaver.ui.interstitialAdManager

@Composable
fun VideoScreen(video: StatusVideo) {
    val context = LocalContext.current
    val nc = LocalNavController.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build()}

    DisposableEffect(exoPlayer) {
        with(exoPlayer) {
            setMediaItem(MediaItem.fromUri(video.path))
            playWhenReady = true
            prepare()
        }
        onDispose { exoPlayer.release() }
    }

    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
            factory = { context ->
                StyledPlayerView(context).apply {
                    player = exoPlayer
                }
            }
        )
    }

    BackHandler {
        interstitialAd(
            activity = context as Activity,
            interstitialAdManager = interstitialAdManager,
            doLast = {
                nc.pop()
            }
        )
    }
}