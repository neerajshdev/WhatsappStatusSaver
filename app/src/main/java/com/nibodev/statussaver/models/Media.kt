package com.nibodev.statussaver.models

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.graphics.scale
import com.nibodev.statussaver.formatTime
import com.nibodev.statussaver.usecase.LoadContentImageUseCase
import com.nibodev.statussaver.usecase.LoadImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Represents Video and Image Content.
 * it knows how to draw it self on screen because it is UIComponent
 *
 * @param location: this describe the path of the media on the storage
 */
interface Media {
    val location: String
}

class ImageMedia(
    override val location: String,
) : Media {
    @Composable
    fun Draw(modifier: Modifier = Modifier) {
        val density = LocalDensity.current
        var previewImage by remember { mutableStateOf<Bitmap?>(null) }
        BoxWithConstraints(modifier = modifier) {
            if (previewImage != null) {
                Image(
                    bitmap = previewImage!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            val contentResolver = LocalContext.current.contentResolver
            LaunchedEffect(Unit) {
                with(density) {
                    previewImage = if (location.startsWith("content")) {
                        LoadContentImageUseCase(
                            contentResolver,
                            Uri.parse(location),
                            maxWidth.roundToPx(),
                            maxHeight.roundToPx()
                        ).invoke()
                    } else {
                        LoadImageUseCase(
                            location,
                            maxWidth.roundToPx(),
                            maxHeight.roundToPx()
                        ).invoke()
                    }
                }
            }
        }
    }
}


class VideoMedia(
    override val location: String
) : Media {

    @Composable
    fun Draw(modifier: Modifier = Modifier) {
        val density = LocalDensity.current
        val context = LocalContext.current
        var previewImage: Bitmap? by remember {mutableStateOf(null)}
        BoxWithConstraints(modifier = modifier) {
            if (previewImage != null) {
                Image(
                    bitmap = previewImage!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Todo: show something else for the sake of feedback
            }

            val contentResolver = LocalContext.current.contentResolver
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    getVideoMetaData(context, location)?.use {
                        try {
                            with(density) {
                                previewImage = it.getFrameAtTime(1000_000)
                                    ?.scale(maxWidth.roundToPx(), maxHeight.roundToPx())
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
            }
        }
    }


}

fun MediaMetadataRetriever.use(func: (MediaMetadataRetriever) -> Unit) {
    func(this)
    release()
}


fun getVideoMetaData(context: Context, src: String): MediaMetadataRetriever? {
    return try {
        val vmr = MediaMetadataRetriever()
        if (src.startsWith("content")) {
            vmr.setDataSource(context, Uri.parse(src))
        } else {
            vmr.setDataSource(src)
        }
        vmr
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}
