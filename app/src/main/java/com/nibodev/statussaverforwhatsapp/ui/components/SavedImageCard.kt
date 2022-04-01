package com.nibodev.statussaverforwhatsapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun SavedImageCard(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    onImageClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Image(
            bitmap = image, contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(320.dp)
                .clickable(onClick = onImageClick)
        )
    }
}