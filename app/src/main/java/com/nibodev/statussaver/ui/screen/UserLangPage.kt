package com.nibodev.statussaver.ui.screen

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import androidx.activity.compose.BackHandler
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nibodev.statussaver.MainActivity
import com.nibodev.statussaver.R
import com.nibodev.statussaver.interstitialAd
import com.nibodev.statussaver.navigation.LocalNavController
import com.nibodev.statussaver.ui.components.NativeSmallAdUnit
import com.nibodev.statussaver.ui.interstitialAdManager
import com.nibodev.statussaver.ui.langNativeAdManager
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import kotlinx.coroutines.launch


@Composable
fun UserLangPage(
) {
    val navController = LocalNavController.current
    Scaffold(
        topBar = { com.nibodev.statussaver.ui.components.TopAppBar(title = stringResource(R.string.top_bar_title))}
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var userLang by remember {
            mutableStateOf("unknown")
        }

        // when the user press on fab button we save the prefs
        // and navigate to homepage
        fun savePref() {
            scope.launch {
                context.getSharedPreferences("settings", MODE_PRIVATE).edit().putString("userLanguage", userLang).apply()
                val activity = context as MainActivity
                val locale = java.util.Locale(userLang, "in")
                with(activity.resources) {
                    configuration.setLocale(locale)
                    updateConfiguration(configuration, displayMetrics)
                }
                interstitialAd(
                    context as Activity,
                    interstitialAdManager = interstitialAdManager,
                    doLast = {
                        navController.push {
                            Homepage()
                        }
                    }
                )
            }
        }

        LaunchedEffect(Unit) {
            val prefs = context.getSharedPreferences("settings", MODE_PRIVATE)
            userLang = prefs.getString("userLanguage", "en") ?: "en"
        }

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            val (nativeAd, english, hindi, fab) = createRefs()

            NativeSmallAdUnit(
                nativeAdManager = langNativeAdManager,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .constrainAs(nativeAd) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                        bottom.linkTo(english.top)
                    },
            )

            Lang(
                lang = stringResource(R.string.user_lang_english),
                bgColor = Color(0xffD4F1F4),
                isChecked = { userLang == "en" },
                onChecked = { userLang = "en" },
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .constrainAs(english) {
                        centerHorizontallyTo(parent)
                        top.linkTo(nativeAd.bottom)
                        bottom.linkTo(hindi.top)
                    }
            )

            Lang(
                lang = stringResource(R.string.user_lang_hindi),
                bgColor = Color(0xffD4F1F4),
                isChecked = { userLang == "hi" },
                onChecked = { userLang = "hi" },
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .constrainAs(hindi) {
                        bottom.linkTo(fab.top)
                        top.linkTo(english.bottom)
                    }
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
            createVerticalChain(english, hindi, chainStyle = ChainStyle.Packed)
        }
    }

    BackHandler {
        navController.push {
            ExitConfirmPage()
        }
    }
}


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
        UserLangPage()
    }
}