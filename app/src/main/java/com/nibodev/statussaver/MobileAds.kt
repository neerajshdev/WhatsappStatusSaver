package com.nibodev.statussaver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.IntRange
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
val shouldReqAd by lazy {
    Firebase.remoteConfig.getBoolean("should_show_ad")
}

val reqAdAfterCount by lazy {
    Firebase.remoteConfig.getString("show_ad_after_count").toInt()
}

var count = 0xffff

suspend fun fetchAdConfig(): Boolean {
    val deferred = CompletableDeferred<Boolean>()
    Firebase.remoteConfig.fetchAndActivate()
        .addOnCompleteListener { task ->
            deferred.complete(task.isSuccessful)
            val TAG = "Firebase config"
            if (task.isSuccessful) {
                isDebug {
                    Log.d(TAG, "Config params updated: ${task.result}")
                    Log.d(TAG, "app_open_ad: ${Firebase.remoteConfig.getString("app_open_ad")}")
                    Log.d(TAG, "interstitial_ad: ${Firebase.remoteConfig.getString("interstitial_ad")}")
                    Log.d(TAG, "home_native_ad: ${Firebase.remoteConfig.getString("home_native_ad")}")
                    Log.d(TAG, "share_text_body: ${Firebase.remoteConfig.getString("share_text_body")}")
                    Log.d(TAG, "status_saver_native_ad = ${Firebase.remoteConfig.getString("status_saver_native_ad")}")
                    Log.d(TAG, "banner_ad = ${Firebase.remoteConfig.getString("banner_ad")}")
                    Log.d(TAG, "exit_confirm_native_ad = ${Firebase.remoteConfig.getString("exit_confirm_native_ad")}")
                    Log.d(TAG, "lang_native_ad = ${Firebase.remoteConfig.getString("lang_native_ad")}")
                    Log.d(TAG, "tabs_ad_threshold = ${Firebase.remoteConfig.getString("status_tab_swipe_ad_threshold")}")
                    Log.d(TAG, "interstitial_ad_threshold = ${Firebase.remoteConfig.getString("interstitial_ad_clicks_threshold")}")
                }
            } else {
                isDebug {
                    Log.d(TAG, "Could not fetch ad configurations")
                }
            }
        }
    return deferred.await()
}


/* Interstitial Ad Unit */
fun loadInterstitialAd(context: Context, onAdLoaded: (InterstitialAd) -> Unit) {
    count++
    if (!shouldReqAd || count < reqAdAfterCount) {
        isDebug {
            if (shouldReqAd)
                Log.d(TAG, "Ad Canceled, count: $count < reqAdAfterCount: $reqAdAfterCount")
            else {
                Log.d(TAG, "Ad Canceled, should req ad: $shouldReqAd")
            }
        }
        return
    }
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
                isDebug { Log.d(TAG, "Failed to load Ad.") }
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                isDebug { Log.d(TAG, "Ad was loaded.") }
                adFullScreenContentCallback(ad)
                onAdLoaded.invoke(ad)
            }
        })
}


private fun adFullScreenContentCallback(ad: InterstitialAd) {
    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {

        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {

        }

        override fun onAdShowedFullScreenContent() {

        }
    }
}


/* Native Ad Unit */
fun loadNativeAdUnits(
    context: Context,
    onAdLoaded: (NativeAd) -> Unit
) {
    if (!shouldReqAd) return
    val videoOptions = VideoOptions.Builder()
        .setStartMuted(false)
        .build()

    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()

    val key = context.resources.getString(R.string.key_native_ad_id)
    val adId = Firebase.remoteConfig.getString(key)

    val adLoader = AdLoader.Builder(context, adId)
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


/**
 * it Loads a single ad and show it on the screen
 */
class InterstitialAdScope {
    // Full screen content callback
    private var fscc: FSCC = FSCC()

    // full screen callback configuration
    fun fsccConfig(config: FSCC.() -> Unit) {
        fscc.config()
    }

    // on ad failed to load callback
    var onFailed: (() -> Unit)? = null

    inner class FSCC {
        var onAdClicked: (() -> Unit)? = null
        var onAdDismissed: (() -> Unit)? = null
        var onAdFailedToShowFullScreenContent: (() -> Unit)? = null
        var onAdImpression: (() -> Unit)? = null
        var onAdShowedFullScreenContent: (() -> Unit)? = null
        fun convert(): FullScreenContentCallback {
            return object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    onAdClicked?.invoke()
                }

                override fun onAdDismissedFullScreenContent() {
                    onAdDismissed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onAdFailedToShowFullScreenContent?.invoke()
                }

                override fun onAdImpression() {
                    onAdImpression?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    onAdShowedFullScreenContent?.invoke()
                }
            }
        }
    }

    fun execute() {
        if (AdManager.instance == null) {
            onFailed?.invoke()
            return
        }
        AdManager.instance?.let { adManager ->
            val interstitialAd = adManager.getInterstitialAd()
            if (interstitialAd == null) {
                onFailed?.invoke()
            } else {
                interstitialAd.fullScreenContentCallback = fscc.convert()
                interstitialAd.show(adManager.getCurrentContext() as Activity)
            }
        }
    }
}


/**
 * it Loads a single ad and show it on the screen
 */
class AppOpenAdScope {
    // Full screen content callback
    private var fscc: FSCC = FSCC()

//    // ad id
//    var id: String? = null
//    var activity: Activity? = null

    // full screen callback configuration
    fun fsccConfig(config: FSCC.() -> Unit) {
        fscc.config()
    }

    // on ad failed to load callback
    var onFailed: (() -> Unit)? = null

    inner class FSCC {
        var onAdClicked: (() -> Unit)? = null
        var onAdDismissed: (() -> Unit)? = null
        var onAdFailedToShowFullScreenContent: (() -> Unit)? = null
        var onAdImpression: (() -> Unit)? = null
        var onAdShowedFullScreenContent: (() -> Unit)? = null

        fun convert(): FullScreenContentCallback {
            return object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    console("ad clicked.")
                    onAdClicked?.invoke()
                }

                override fun onAdDismissedFullScreenContent() {
                    console("dismissed full screen content.")
                    onAdDismissed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {

                    onAdFailedToShowFullScreenContent?.invoke()
                }

                override fun onAdImpression() {
                    console("recorded an ad impression.")
                    onAdImpression?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    console("showed an full screen app open ad.")
                    onAdShowedFullScreenContent?.invoke()
                }
            }
        }
    }

    suspend fun execute() {
        if (AdManager.instance == null) {
            onFailed?.invoke()
            return
        }
        AdManager.instance?.let { adManager ->
            val appOpenAd = adManager.getAppOpenAd()
            if (appOpenAd == null) {
                onFailed?.invoke()
            } else {
                appOpenAd.fullScreenContentCallback = fscc.convert()
                appOpenAd.show(adManager.getCurrentContext() as Activity)
            }
        }
    }
}

/**
 * Interstitial ad dsl
 */
fun interstitialAd(
    config: InterstitialAdScope.() -> Unit
) = with(InterstitialAdScope()) {
    config()
    execute()
}

/**
 * appOpenAd dsl
 */
suspend fun appOpenAd(
    config: AppOpenAdScope.() -> Unit
) = with(AppOpenAdScope()) {
    config()
    execute()
}




data class AdIds(
    val nativeAdId: String,
    val appOpenAdId: String,
    val interstitialAdId: String,
)

/*** this manages ads and loading of them **/
class AdManager(var context: Context, private val adIds: AdIds) {
    private val nativeAds = mutableListOf<NativeAd>()
    val interstitialAds = mutableListOf<InterstitialAd>()
    private val nativeAdLoader by lazy {
        AdLoader.Builder(context, adIds.nativeAdId)
            .forNativeAd { nativeAd ->
                nativeAds.add(nativeAd)
            }
            .withAdListener(
                object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                    }
                }
            )
            .build()
    }

    @SuppressLint("StaticFieldLeak")
    companion object {
        var instance: AdManager? = null
        fun createInstance(context: Context, adIds: AdIds): AdManager {
            return instance ?: synchronized(this) {
                AdManager(context, adIds)
                    .also { instance = it }
            }
        }
    }

    /** todo: update this fun to observe activity lifecycle
     */
    fun getCurrentContext(): Context = context

    private fun loadNativeAds(num: Int) {
        val adRequest = AdRequest.Builder().build()
        nativeAdLoader.loadAds(adRequest, 5 - nativeAds.size)
    }

    fun getInterstitialAd(): InterstitialAd? {
        if (interstitialAds.isEmpty()) {
            loadNativeAds(5)
            return null
        }
        interstitialAds.add(interstitialAds.removeFirst())
        return interstitialAds.last()
    }

    val appOpenAdResult = CompletableDeferred<AppOpenAd?>()
    suspend fun getAppOpenAd(): AppOpenAd? {
        val adRequest = AdManagerAdRequest.Builder().build()
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                appOpenAdResult.complete(null)
            }

            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                appOpenAdResult.complete(appOpenAd)
            }
        }
        AppOpenAd.load(
            context,
            adIds.appOpenAdId,
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            loadCallback
        )
        return appOpenAdResult.await()
    }
}
*/


/** Ask this class object when to show the ad
 * @param threshold minimum time in milli seconds,
 * after which you can load ads
 */
class AdCounter(private val threshold: Int) {
    private var clicks = 0
    fun timeToShow(): Boolean {
        clicks++
        val result = clicks > threshold
        if (result) {
            clicks = 0
        }
        return result
    }
}

class AppOpenAdManager(
    val adId: String,
    val bufferSize: Int = 1
) {
    var reqCount = 0
    val ads = mutableListOf<AppOpenAd>()
    val adRequest
        get() = AdRequest.Builder().build()


    val loadCallback
        get() = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                reqCount--
                ads.add(ad)
                console("loaded an app open ad, available : ${ads.size}: $this")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                reqCount--
            }
        }

    fun getAd(context: Context): AppOpenAd? {
        if (ads.isEmpty()) {
            console("app open ad request count: $reqCount")
            if (reqCount <= 0) {
                preFetch(context)
            }
            return null
        }
        return ads.removeFirst().also { preFetch(context)}
    }

    private fun loadAppOpenAd(context: Context, numOfAds: Int) {
        repeat(numOfAds) {
            reqCount++
            AppOpenAd.load(
                context,
                adId,
                adRequest,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                loadCallback
            )
        }
    }

    fun preFetch(context: Context) {
        loadAppOpenAd(context, bufferSize - ads.size)
    }
}

/**
 * App open ad builder fun.
 * If the ad can be loaded in time
 * then show it otherwise doLast
 */
suspend fun appOpenAd(
    activity: Activity,
    appOpenAdManager: AppOpenAdManager,
    onAdClicked: (() -> Unit)? = null,
    onAdDismissedFsc: (() -> Unit)? = null,
    onAdImpression: (() -> Unit)? = null,
    onAdShowFsc: (() -> Unit)? = null,
    onAdFailedToShowFsc: (() -> Unit)? = null,
    doLast: (() -> Unit)? = null,
    tryCount: Int = 1,
    delayTime: Long = 500
) {
    var tried = 0
    var ad: AppOpenAd? = appOpenAdManager.getAd(activity)
    while (ad == null && tried < tryCount) {
        delay(delayTime)
        ad = appOpenAdManager.getAd(activity)
        tried++
        console("tried = $tried")
    }

    if (ad != null) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                onAdClicked?.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                doLast?.invoke()
                onAdDismissedFsc?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                doLast?.invoke()
                onAdFailedToShowFsc?.invoke()
            }

            override fun onAdImpression() {
                onAdImpression?.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                onAdShowFsc?.invoke()
            }
        }
        ad.show(activity)
    } else {
        if (doLast != null) {
            console("failed to load app open ad!")
            doLast()
        }
    }
}


class InterstitialAdManager(
    val adId: String,
    val bufferSize: Int = 5
) {
    var reqCount = 0
    val ads = mutableListOf<InterstitialAd>()
    val adRequest
        get() = AdRequest.Builder().build()


    val loadCallback
        get() = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                reqCount--
                ads.add(ad)
                console("loaded an interstitial ad, available : ${ads.size}")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                reqCount--
            }
        }

    fun getAd(context: Context): InterstitialAd? {
        if (ads.isEmpty()) {
            console("inter ad request count: $reqCount")
            if (reqCount <= 0) {
                prefetch(context)
            }
            return null
        }
        return ads.removeFirst().also { prefetch(context) }
    }

    private fun loadAd(context: Context, numOfAds: Int) {
        repeat(numOfAds) {
            reqCount++
            InterstitialAd.load(
                context,
                adId,
                adRequest,
                loadCallback
            )
        }
    }

    fun prefetch(context: Context) {
        loadAd(context, bufferSize - ads.size)
    }
}

fun interstitialAd(
    activity: Activity,
    interstitialAdManager: InterstitialAdManager,
    interAdCounter: AdCounter? = null,
    onAdClicked: (() -> Unit)? = null,
    onAdDismissedFsc: (() -> Unit)? = null,
    onAdImpression: (() -> Unit)? = null,
    onAdShowFsc: (() -> Unit)? = null,
    onAdFailedToShowFsc: (() -> Unit)? = null,
    doLast: (() -> Unit)? = null
) {
    if (interAdCounter != null && !interAdCounter.timeToShow()) {
        doLast?.invoke()
        return
    }
    interstitialAdManager.getAd(activity).also { interstitialAd ->
        if (interstitialAd != null) {
            interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    onAdClicked?.invoke()
                }

                override fun onAdDismissedFullScreenContent() {
                    onAdDismissedFsc?.invoke()
                    if (doLast != null) {
                        doLast()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onAdFailedToShowFsc?.invoke()
                    if (doLast != null) {
                        doLast()
                    }
                }

                override fun onAdImpression() {
                    onAdImpression?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    onAdShowFsc?.invoke()
                }
            }
            interstitialAd.show(activity)
        } else {
            doLast?.invoke()
        }
    }
}


class NativeAdManager(
    val adId: String,
    @IntRange(from = 1, to = 5) val bufferSize: Int = 1
) {
    val ads = mutableListOf<NativeAd>()
    val adRequest
        get() = AdRequest.Builder().build()

    private var adLoader: AdLoader? = null
    private var reqCount = 0


    fun getAd(context: Context): NativeAd? {
        if (ads.isEmpty()) {
            if(reqCount <= 0) {
                prefetch(context)
            }
            return null
        }
        return ads.removeFirst().also { prefetch(context) }
    }

    private fun loadNativeAd(context: Context, numOfAds: Int) {
        if (adLoader == null) {
            val videoOptions = VideoOptions.Builder()
                .setStartMuted(false)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()
            adLoader = AdLoader.Builder(context, adId)
                .forNativeAd { nativeAd ->
                    ads.add(nativeAd)
                    console("loaded a native ad, available : ${ads.size} : native ad manager: $this")
                }
                .withAdListener(
                    object : AdListener() {
                        override fun onAdClicked() {

                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            reqCount--
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                        }

                        override fun onAdOpened() {
                            super.onAdOpened()
                        }
                    }
                )
                .withNativeAdOptions(adOptions)
                .build()
        }

        reqCount += numOfAds
        adLoader?.loadAds(adRequest, numOfAds)
    }

    fun prefetch(context: Context) {
        loadNativeAd(context, bufferSize - ads.size)
    }
}


suspend fun fetchAdConfig(): Boolean {
    val deferred = CompletableDeferred<Boolean>()
    Firebase.remoteConfig.fetchAndActivate()
        .addOnCompleteListener { task ->
            deferred.complete(task.isSuccessful && task.result == true)
            val TAG = "Firebase config"
            if (task.isSuccessful) {
                isDebug {
                    Log.d(TAG, "Config params updated: ${task.result}")
                    Log.d(TAG, "app_open_ad: ${Firebase.remoteConfig.getString("app_open_ad")}")
                    Log.d(TAG, "interstitial_ad: ${Firebase.remoteConfig.getString("interstitial_ad")}")
                    Log.d(TAG, "home_native_ad: ${Firebase.remoteConfig.getString("home_native_ad")}")
                    Log.d(TAG, "share_text_body: ${Firebase.remoteConfig.getString("share_text_body")}")
                    Log.d(TAG, "status_saver_native_ad = ${Firebase.remoteConfig.getString("status_saver_native_ad")}")
                    Log.d(TAG, "banner_ad = ${Firebase.remoteConfig.getString("banner_ad")}")
                    Log.d(TAG, "exit_confirm_native_ad = ${Firebase.remoteConfig.getString("exit_confirm_native_ad")}")
                    Log.d(TAG, "lang_native_ad = ${Firebase.remoteConfig.getString("lang_native_ad")}")
                    Log.d(TAG, "direct_chat_native_ad = ${Firebase.remoteConfig.getString("direct_chat_native_ad")}")
                    Log.d(TAG, "tabs_ad_threshold = ${Firebase.remoteConfig.getString("status_tab_swipe_ad_threshold")}")
                    Log.d(TAG, "interstitial_ad_threshold = ${Firebase.remoteConfig.getString("interstitial_ad_clicks_threshold")}")
                }
            } else {
                isDebug {
                    Log.d(TAG, "Could not fetch ad configurations")
                }
            }
        }
    return deferred.await()
}


