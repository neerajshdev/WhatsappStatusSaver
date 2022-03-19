package com.softneez.wasaver

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

val shouldReqAd by lazy {
    Firebase.remoteConfig.getBoolean("should_show_ad")
}

val reqAdAfterCount by lazy {
    Firebase.remoteConfig.getString("show_ad_after_count").toInt()
}

var count = 0

fun fetchAdConfig(onSuccess: () -> Unit) {
    Firebase.remoteConfig.fetchAndActivate()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updated = task.result
                if (updated) onSuccess()
                isDebug {
                    Log.d(TAG, "Config params updated: $updated")
                    Log.d(TAG, "should_show_ad: ${Firebase.remoteConfig.getBoolean("should_show_ad")}")
                    Log.d(TAG, "share_sub_body: ${Firebase.remoteConfig.getString("share_sub_body")}")
                    Log.d(TAG, "share_text_body: ${Firebase.remoteConfig.getString("share_text_body")}")
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
    count++
    if(!shouldReqAd || count < reqAdAfterCount) return
    count = 0

    val key: String = if (Random.nextBoolean()) {
        context.getString(R.string.key_interstitial_video_add_id)
    } else {
        context.getString(R.string.key_interstitial_add_id)
    }

    val adId = Firebase.remoteConfig.getString(key)

    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        context,
        adId,
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
               isDebug { Log.d(TAG, adError.message) }
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                isDebug {  Log.d(TAG, "Ad was loaded.") }
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
    onAdLoaded: (NativeAd) -> Unit
) {
    if(!shouldReqAd) return
    val videoOptions = VideoOptions.Builder()
        .setStartMuted(false)
        .build()

    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()

    val key = context.resources.getString(R.string.key_native_ad_id)
    val adId = Firebase.remoteConfig.getString(key)

    val adLoader = AdLoader.Builder(context, adId )
        .forNativeAd { ad ->
            if ((context as Activity).isDestroyed)
                return@forNativeAd

            Log.d(TAG, "Add loaded $ad")
            onAdLoaded(ad)
        }
        .withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    isDebug {
                        Log.d(TAG, "native ad loading failed")
                    }
                }
            }
        )
        .withNativeAdOptions(adOptions)
        .build()

    adLoader.loadAd(AdRequest.Builder().build())
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
    adView.mediaView!!.setMediaContent(nativeAd.mediaContent!!)


    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
        adView.bodyView!!.visibility = View.INVISIBLE
    } else {
        adView.bodyView!!.visibility = View.VISIBLE
        (adView.bodyView as TextView).text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
        adView.callToActionView!!.visibility = View.INVISIBLE
    } else {
        adView.callToActionView!!.visibility = View.VISIBLE
        (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
        adView.iconView!!.visibility = View.GONE
    } else {
        (adView.iconView as ImageView).setImageDrawable(
            nativeAd.icon!!.drawable
        )
        adView.iconView!!.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
        adView.priceView!!.visibility = View.INVISIBLE
    } else {
        adView.priceView!!.visibility = View.VISIBLE
        (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
        adView.storeView!!.visibility = View.INVISIBLE
    } else {
        adView.storeView!!.visibility = View.VISIBLE
        (adView.storeView!! as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
        adView.starRatingView!!.visibility = View.INVISIBLE
    } else {
        (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
        adView.starRatingView!!.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
        adView.advertiserView!!.visibility = View.INVISIBLE
    } else {
        (adView.advertiserView as TextView).text = nativeAd.advertiser
        adView.advertiserView!!.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)
}
