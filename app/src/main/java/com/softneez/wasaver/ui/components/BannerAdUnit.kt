package com.softneez.wasaver.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.softneez.wasaver.R

@Composable
fun BannerAdUnit(modifier: Modifier = Modifier) {
    val adIdKey = stringResource(id =  R.string.key_banner_add_id)
    AndroidView(
        factory = {
            val adView = AdView(it)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = Firebase.remoteConfig.getString(adIdKey)
            adView
        }, update = { adView ->
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        },
        modifier = modifier
    )
}


