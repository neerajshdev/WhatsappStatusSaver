package com.nibodev.statussaverforwhatsapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun RecentImageCard(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    isSaved: Boolean,
    onImageClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Box(modifier = modifier) {
        Image(
            bitmap = image, contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(320.dp)
                .clickable(onClick = onImageClick)
        )
        DownloadStrip(
            downloadDone = isSaved, onClick = { onDownloadClick() }, modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(320.dp)
        )
    }
}