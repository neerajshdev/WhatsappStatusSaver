package com.nibodev.statussaver.ui.screen

import android.app.Activity
import android.widget.Toast
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
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.nibodev.statussaver.interstitialAd
import com.nibodev.statussaver.models.VideoMedia
import com.nibodev.statussaver.navigation.LocalNavController
import com.nibodev.statussaver.ui.interstitialAdManager
import java.io.File

@Composable
fun VideoPage(video: VideoMedia) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build()}

    DisposableEffect(exoPlayer) {
        with(exoPlayer) {
            try{
                if(video.location.startsWith("content")) {
                    setMediaItem(MediaItem.fromUri(video.location))
                } else {
                    setMediaItem(MediaItem.fromUri(File(video.location).toUri()))
                }
                playWhenReady = true
                prepare()
            }catch (ex: Exception){
                ex.printStackTrace()
                Toast.makeText(context, "something went wrong..", Toast.LENGTH_LONG
                ).show()
            }

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
        navController.pop()
        interstitialAd(
            activity = context as Activity,
            interstitialAdManager = interstitialAdManager,
        )
    }
}
