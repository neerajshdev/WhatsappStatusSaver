package com.nibodev.statussaver.ui.components

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nibodev.statussaver.loadNativeAdUnits
import com.nibodev.statussaver.populateNativeAdView
import com.nibodev.statussaver.shouldReqAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.nibodev.statussaver.R


@Composable
fun NativeAdUnit(modifier: Modifier = Modifier) {
    var ad by remember {
        mutableStateOf<NativeAd?>(null)
    }

    val context = LocalContext.current

    LaunchedEffect(context) {
        withContext(Dispatchers.Default) {
           loadNativeAdUnits(context) {
               ad = it
           }
        }
    }

    DisposableEffect(context) {
        onDispose {
            ad?.destroy()
        }
    }

    if(shouldReqAd)
    Box(modifier = modifier.padding(horizontal = 8.dp).size(360.dp)) {
        if (ad != null) {
            AndroidView(
                factory = {
                    val v = LayoutInflater.from(it).inflate(R.layout.native_ad_view, null) as NativeAdView
                    populateNativeAdView(ad!!, v)
                    v
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}