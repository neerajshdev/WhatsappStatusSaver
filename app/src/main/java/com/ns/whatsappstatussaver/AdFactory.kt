package com.ns.whatsappstatussaver

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlin.random.Random


fun fetchAdConfig(onSuccess: () -> Unit) {
    Firebase.remoteConfig.fetchAndActivate()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updated = task.result
                if (updated) onSuccess()
                isDebug {
                    Log.d(TAG, "Config params updated: $updated")
                    Log.d(TAG, "app_id = ${Firebase.remoteConfig.getString("app_id")}")
                    Log.d(TAG, "native = ${Firebase.remoteConfig.getString("native")}")
                    Log.d(TAG, "banner = ${Firebase.remoteConfig.getString("banner")}")
                    Log.d(TAG, "interstitial = ${Firebase.remoteConfig.getString("interstitial")}")
                    Log.d(TAG, "interstitial_video = ${Firebase.remoteConfig.getString("interstitial_video")}")
                }
            } else {
                isDebug {
                    Log.d(TAG, "Could not fetch ad configurations")
                }
            }
        }
}


/* Interstitial Ad Unit */
fun loadInterstitialAd(context: Context, onAdLoaded: (InterstitialAd) -> Unit) {

    var key = ""
    key = if (Random.nextBoolean()) {
        "interstitial_video"
    } else {
        "interstitial"
    }

    val adId = Firebase.remoteConfig.getString(key)
    Log.d(TAG, "$key = $adId")

    if (adId.isBlank()) {
        fetchAdConfig() {
            loadInterstitialAd(context, onAdLoaded)
        }
        return
    }
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        context,
        adId,
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                adFullScreenContentCallback(ad)
                onAdLoaded.invoke(ad)
            }
        })
}


private fun adFullScreenContentCallback(ad: InterstitialAd) {
    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            Log.d(TAG, "Ad was dismissed.")
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            Log.d(TAG, "Ad failed to show.")
        }

        override fun onAdShowedFullScreenContent() {
            Log.d(TAG, "Ad showed fullscreen content.")
        }
    }
}


/* Native Ad Unit */
fun loadNativeAdUnits(
    context: Context,
    count: Int,
    onAdLoaded: (NativeAd) -> Unit
) {
    val videoOptions = VideoOptions.Builder()
        .setStartMuted(false)
        .build()

    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()

    val adId = Firebase.remoteConfig.getString("native")

    if (adId.isBlank()) {
        fetchAdConfig() {
            loadNativeAdUnits(context, count, onAdLoaded)
        }
        return
    }

    val adLoader = AdLoader.Builder(context, context.resources.getString(R.string.native_ad_id))
        .forNativeAd { ad ->
            if ((context as Activity).isDestroyed)
                return@forNativeAd

            Log.d(TAG, "Add loaded $ad")
            onAdLoaded(ad)
        }
        .withNativeAdOptions(adOptions)
        .build()

    adLoader.loadAds(AdRequest.Builder().build(), count)
}


fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // Set the media view.
    adView.mediaView = adView.findViewById(R.id.ad_media)

    // Set other ad assets.
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)
    adView.priceView = adView.findViewById(R.id.ad_price)
    adView.starRatingView = adView.findViewById(R.id.ad_stars)
    adView.storeView = adView.findViewById(R.id.ad_store)
    adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

    // The headline and media content are guaranteed to be in every UnifiedNativeAd.
    (adView.headlineView as TextView).text = nativeAd.headline
    adView.mediaView.setMediaContent(nativeAd.mediaContent)


    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
        adView.bodyView.visibility = View.INVISIBLE
    } else {
        adView.bodyView.visibility = View.VISIBLE
        (adView.bodyView as TextView).text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
        adView.callToActionView.visibility = View.INVISIBLE
    } else {
        adView.callToActionView.visibility = View.VISIBLE
        (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
        adView.iconView.visibility = View.GONE
    } else {
        (adView.iconView as ImageView).setImageDrawable(
            nativeAd.icon.drawable
        )
        adView.iconView.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
        adView.priceView.visibility = View.INVISIBLE
    } else {
        adView.priceView.visibility = View.VISIBLE
        (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
        adView.storeView.visibility = View.INVISIBLE
    } else {
        adView.storeView.visibility = View.VISIBLE
        (adView.storeView as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
        adView.starRatingView.visibility = View.INVISIBLE
    } else {
        (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
        adView.starRatingView.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
        adView.advertiserView.visibility = View.INVISIBLE
    } else {
        (adView.advertiserView as TextView).text = nativeAd.advertiser
        adView.advertiserView.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)
}
