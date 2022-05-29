package com.nibodev.statussaver.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nibodev.statussaver.AppOpenAdManager
import com.nibodev.statussaver.InterstitialAdManager
import com.nibodev.statussaver.NativeAdManager

val appOpenAdManager = AppOpenAdManager("ca-app-pub-3940256099942544/3419835294", 5)
val interstitialAdManager = InterstitialAdManager("ca-app-pub-3940256099942544/1033173712", 5)

val exitConfirmNativeAdManager = NativeAdManager("ca-app-pub-3940256099942544/2247696110", 5)
val homeNativeAdManager = NativeAdManager("ca-app-pub-3940256099942544/2247696110", 5)
val statusSaverNativeAdManager = NativeAdManager("ca-app-pub-3940256099942544/2247696110", 5)
val langNativeAdManager = NativeAdManager("ca-app-pub-3940256099942544/2247696110", 5)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val USER_LANG = stringPreferencesKey("USER-LANG")
val AD_CONFIG_LOADED = booleanPreferencesKey("ad-config-loaded")