package com.nibodev.statussaver.ui

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.nibodev.statussaver.AdCounter
import com.nibodev.statussaver.AppOpenAdManager
import com.nibodev.statussaver.InterstitialAdManager
import com.nibodev.statussaver.NativeAdManager

val appOpenAdManager by lazy  {AppOpenAdManager(Firebase.remoteConfig.getString("app_open_ad"), 1)}
val interAdCounter by lazy {
    val threshold =  try {Firebase.remoteConfig.getString("interstitial_ad_clicks_threshold").toInt()} catch (ex: Exception) {3}
    AdCounter(threshold)
}
val interstitialAdManager by lazy {  InterstitialAdManager(Firebase.remoteConfig.getString("interstitial_ad"), 1)}

val exitConfirmNativeAdManager by lazy {   NativeAdManager(Firebase.remoteConfig.getString("exit_confirm_native_ad"), 1) }
val homeNativeAdManager by lazy {   NativeAdManager(Firebase.remoteConfig.getString("home_native_ad"), 1)}
val statusSaverNativeAdManager by lazy {   NativeAdManager(Firebase.remoteConfig.getString("status_saver_native_ad"), 1)}
val langNativeAdManager by lazy {NativeAdManager(Firebase.remoteConfig.getString("lang_native_ad"), 1)}
val directChatNativeAdManager by lazy {NativeAdManager(Firebase.remoteConfig.getString("direct_chat_native_ad"), 1)}
