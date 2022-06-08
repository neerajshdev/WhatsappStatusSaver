package com.nibodev.statussaver.ui.screen

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nibodev.statussaver.*
import com.nibodev.statussaver.navigation.LocalNavController
import com.nibodev.statussaver.ui.appOpenAdManager
import com.nibodev.statussaver.ui.components.OnBackgroundImage
import com.nibodev.statussaver.ui.interstitialAdManager
import com.nibodev.statussaver.ui.langNativeAdManager
import com.nibodev.statussaver.ui.theme.brightWhite
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive

@Composable
fun LoadingPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    OnBackgroundImage(
        painter = painterResource(R.drawable.bg),
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = brightWhite
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.drawable_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 32.dp)
                .size(144.dp, 144.dp)
                .align(alignment = Alignment.Center)
        )


        Text(
            text = "Status Saver\nfor Whatsapp",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }

    // load all the stuff here
    LaunchedEffect(Unit) {
        val pref = context.getSharedPreferences("settings", MODE_PRIVATE)
        // load ad config from firebase

        var configLoaded = pref.getBoolean("firebaseConfig", false)
        if (configLoaded) fetchAdConfig()

        while(!configLoaded) {
            console("loading ad config")
            // ensure that net is available
            if(!isNetworkConnected(context)) {
                Toast.makeText(context, "Network isn't available!", Toast.LENGTH_LONG).show()
                while (!isNetworkConnected(context)) {
                    ensureActive()
                    delay(500)
                }
            }
            configLoaded = fetchAdConfig()
        }
        pref.edit().putBoolean("firebaseConfig", configLoaded).apply()

        langNativeAdManager.prefetch(context)

        // prefetch ads
        interstitialAdManager.prefetch(context)
        appOpenAdManager.preFetch(context)

        appOpenAd(
            context as Activity,
            appOpenAdManager = appOpenAdManager,
            tryCount = 20,
            delayTime = 1000,
            doLast = {
                navController.replace { UserLangPage() }
            }
        )
    }
}
