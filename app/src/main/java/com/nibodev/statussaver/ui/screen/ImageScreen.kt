package com.nibodev.statussaver.ui.screen

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.nibodev.statussaver.InterstitialAdManager
import com.nibodev.statussaver.NativeAdManager
import com.nibodev.statussaver.console
import com.nibodev.statussaver.interstitialAd
import com.nibodev.statussaver.models.StatusImage
import com.nibodev.statussaver.ui.LocalNavController
import com.nibodev.statussaver.ui.interAdCounter
import com.nibodev.statussaver.ui.interstitialAdManager

@Composable
fun ImageScreen(image: StatusImage) {
    val context = LocalContext.current
    val nc = LocalNavController.current
    val bitmap = remember {
        try {
            loadImage(context, image.path)
        }
       catch (ex: Exception) {
           Toast.makeText(context, "something went wrong..", Toast.LENGTH_LONG).show()
           null
       }
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

    BackHandler {
        interstitialAd(
            activity = context as Activity,
            interstitialAdManager = interstitialAdManager,
            interAdCounter = interAdCounter,
            doLast = {
                nc.pop()
            }
        )
    }
}


//private fun loadImage(context: Context, uri: Uri): Bitmap? {
//    var bitmap : Bitmap? = null
//    console("loading image from $uri")
//    context.contentResolver.openFileDescriptor(uri, "r").use {
//        it?.let {
//            bitmap = BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
//        }
//    }
//    return bitmap
//}


fun loadImage(context: Context, from: String): ImageBitmap {
    val bitmap = if (from.startsWith("content")) {
        context.contentResolver.openFileDescriptor(Uri.parse(from), "r").use {
            BitmapFactory.decodeFileDescriptor(it!!.fileDescriptor)
        }
    } else {
        BitmapFactory.decodeFile(from)
    }
    return bitmap.asImageBitmap()
}