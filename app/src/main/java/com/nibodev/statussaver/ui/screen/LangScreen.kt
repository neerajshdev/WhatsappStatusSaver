package com.nibodev.statussaver.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nibodev.statussaver.MainActivity
import com.nibodev.statussaver.NativeAdManager
import com.nibodev.statussaver.R
import com.nibodev.statussaver.ui.LocalNavController
import com.nibodev.statussaver.ui.components.NativeMediumAdUnit
import com.nibodev.statussaver.ui.components.NativeSmallAdUnit
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import kotlinx.coroutines.launch


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val USER_LANG = stringPreferencesKey("USER-LANG")

private val nativeAdManager = NativeAdManager("ca-app-pub-3940256099942544/2247696110", 1)

@Composable
fun LangScreen() {
    val nc = LocalNavController.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var userLang by remember {
        mutableStateOf("unknown")
    }

    fun savePref() {
        scope.launch {
            context.dataStore.edit { pref ->
                pref[USER_LANG] = userLang
            }
            val activity = context as MainActivity
            val locale = java.util.Locale(userLang, "in")
            with(activity.resources) {
                configuration.setLocale(locale)
                updateConfiguration(configuration, displayMetrics)
            }

            nc.replace {
                HomePage()
            }
        }
    }

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { pref ->
            userLang = pref[USER_LANG] ?: "en"
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (nativeAd, english, hindi, fab) = createRefs()

        Box(
            modifier = Modifier.constrainAs(nativeAd) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(english.top, 32.dp)
                    top.linkTo(parent.top, 8.dp)
                }
                .padding(horizontal = 16.dp),
//            nativeAdManager = nativeAdManager
        )

        Lang(
            lang = stringResource(R.string.user_lang_english),
            bgColor = Color(0xffD4F1F4),
            isChecked = { userLang == "en" },
            onChecked = { userLang = "en" },
            modifier = Modifier
                .constrainAs(english) {
                    centerHorizontallyTo(parent)
                    top.linkTo(nativeAd.bottom)
                    bottom.linkTo(hindi.top)
                }
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )

        Lang(
            lang = stringResource(R.string.user_lang_hindi),
            bgColor = Color(0xffD4F1F4),
            isChecked = { userLang == "hi" },
            onChecked = { userLang = "hi" },
            modifier = Modifier
                .constrainAs(hindi) {
                    bottom.linkTo(fab.top)
                    top.linkTo(english.bottom)
                }
                .padding(start = 16.dp, end = 16.dp)
        )

        FloatingActionButton(
            onClick = {
                savePref()
            },
            modifier = Modifier
                .constrainAs(fab) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_arrow_forward_48),
                contentDescription = stringResource(R.string.move_forword),
                modifier = Modifier.padding(8.dp)
            )
        }
        createVerticalChain(nativeAd, english, hindi, chainStyle = ChainStyle.Packed)
    }
}


// todo: implement gesture for button press effect

@Composable
fun Lang(
    modifier: Modifier = Modifier,
    lang: String,
    bgColor: Color,
    textColor: Color = Color(0xff292929),
    isChecked: () -> Boolean,
    onChecked: () -> Unit
) {
    val lottieEnglishHindi by
    rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.lottie_multilang))
    val lottieSelected by
    rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.lottie_selected))

    Card(
        elevation = 4.dp,
        backgroundColor = bgColor,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(74.dp)
            .clickable { onChecked() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LottieAnimation(
                composition = lottieEnglishHindi,
                contentScale = ContentScale.Crop,
                iterations = Int.MAX_VALUE,
                modifier = Modifier
                    .size(100.dp)
                    .weight(0.5f)
            )
            Text(
                text = lang,
                style = MaterialTheme.typography.h4,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            AnimatedVisibility(visible = isChecked()) {
                LottieAnimation(
                    composition = lottieSelected,
                    iterations = 1,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(96.dp)
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun LangScreenPreview() {
    WhatsappStatusSaverTheme {
        LangScreen()
    }
}