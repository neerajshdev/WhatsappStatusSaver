package com.ns.whatsappstatussaver.ui.components

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.ns.whatsappstatussaver.R
import com.ns.whatsappstatussaver.loadNativeAdUnits
import com.ns.whatsappstatussaver.populateNativeAdView
import com.ns.whatsappstatussaver.shouldReqAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NativeAdUnit(modifier: Modifier = Modifier) {
    var ad by remember {
        mutableStateOf<NativeAd?>(null)
    }

    val context = LocalContext.current

    LaunchedEffect(context) {
        withContext(Dispatchers.Default) {
           loadNativeAdUnits(context, 1, ) {
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
    Box(modifier = modifier.size(360.dp)) {
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