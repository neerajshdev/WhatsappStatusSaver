package com.nibodev.composeapp.model

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.nibodev.composeapp.R


sealed class Media(open val preview: ImageBitmap, open val uri: Uri) {
    data class Image(override val preview: ImageBitmap, override val uri: Uri) : Media(preview, uri)
    data class Video(override val preview: ImageBitmap, override val uri: Uri) : Media(preview, uri)
    companion object {
        fun getMedia(context: Context): List<Media> {
            return listOf<Media>(
                Image(
                    preview = BitmapFactory.decodeResource(context.resources, R.drawable.image_1)
                        .asImageBitmap(),
                    uri = Uri.parse("1")
                ),
                Image(
                    preview = BitmapFactory.decodeResource(context.resources, R.drawable.image_2)
                        .asImageBitmap(),
                    uri = Uri.parse("2")
                ),
                Image(
                    preview = BitmapFactory.decodeResource(context.resources, R.drawable.image_3)
                        .asImageBitmap(),
                    uri = Uri.parse("3")
                ),
                Image(
                    preview = BitmapFactory.decodeResource(context.resources, R.drawable.image_1)
                        .asImageBitmap(),
                    uri = Uri.parse("4")
                ),
                Image(
                    preview = BitmapFactory.decodeResource(context.resources, R.drawable.image_2)
                        .asImageBitmap(),
                    uri = Uri.parse("5")
                ),
                Image(
                    preview = BitmapFactory.decodeResource(context.resources, R.drawable.image_3)
                        .asImageBitmap(),
                    uri = Uri.parse("6")
                )
            )
        }
    }
}
