package com.ns.whatsappstatussaver.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.ns.whatsappstatussaver.R

@Composable
fun BannerAdUnit(modifier: Modifier = Modifier) {
    val adId = stringResource(id = R.string.banner_ad_id)
    AndroidView(
        factory = {
            val adView = AdView(it)
            adView.adSize = AdSize.FLUID
            adView.adUnitId = adId
            adView
        }, update = { adView ->
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        },
        modifier = modifier
    )
}


