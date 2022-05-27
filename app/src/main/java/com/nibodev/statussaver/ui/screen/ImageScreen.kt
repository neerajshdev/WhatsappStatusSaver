package com.nibodev.statussaver.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.nibodev.statussaver.models.StatusImage

@Composable
fun ImageScreen(image: StatusImage) {
    val context = LocalContext.current
    val bitmap = remember {
        loadImage(context, image.uri())?.asImageBitmap()
    }

    bitmap?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            Image(
                bitmap = it,
                contentDescription = "full image view",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


private fun loadImage(context: Context, uri: Uri): Bitmap? {
    var bitmap : Bitmap? = null
    context.contentResolver.openFileDescriptor(uri, "r").use {
        it?.let {
            bitmap = BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
    }
    return bitmap
}