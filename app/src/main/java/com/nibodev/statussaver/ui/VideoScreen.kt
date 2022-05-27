package com.nibodev.statussaver.ui

import android.net.Uri
import android.view.ViewGroup
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
import com.nibodev.statussaver.MainViewModel
import java.io.File


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