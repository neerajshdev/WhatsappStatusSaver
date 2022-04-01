package com.nibodev.statussaverforwhatsapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nibodev.statussaverforwhatsapp.R

@Composable
fun RecentVideoCard(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    isSaved: Boolean,
    label: String,
    onImageClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Box(modifier = modifier) {
        Image(
            bitmap = image, contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(320.dp)   // 2 x 2 square inch
                .clickable(onClick = onImageClick)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_round_play_circle_outline_24),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.align(Alignment.Center)
                .size(48.dp)
        )

        Text(
            text = label,
            color= Color.White,
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp)
        )

        DownloadStrip(
            downloadDone = isSaved,
            onClick = { onDownloadClick() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(320.dp)
        )
    }
}
