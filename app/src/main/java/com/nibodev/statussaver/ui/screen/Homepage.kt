package com.nibodev.statussaver.ui.screen

import android.widget.ListView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nibodev.statussaver.*
import com.nibodev.statussaver.navigation.LocalNavController
import com.nibodev.statussaver.ui.components.*
import com.nibodev.statussaver.ui.homeNativeAdManager
import com.nibodev.statussaver.ui.interAdCounter
import com.nibodev.statussaver.ui.interstitialAdManager
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import kotlinx.coroutines.launch
import com.nibodev.statussaver.R

@Composable
fun Homepage() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as MainActivity
    val bgImage = painterResource(R.drawable.bg)
    val directChat = painterResource(R.drawable.tools_direct_chat)
    val share = painterResource(R.drawable.btn_share)
    val statusSaver = painterResource(R.drawable.tools_status_saver)
    val privacy = painterResource(R.drawable.btn_privacy)

    Scaffold(
        topBar = { TopAppBar(title = stringResource(R.string.top_bar_title)) }
    ) {
        OnBackgroundImage(
            painter = bgImage,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Box(
                modifier = Modifier
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = 56.dp)
                ) {
                    item {
                        NativeMediumAdUnit(nativeAdManager = homeNativeAdManager)
                    }
                    item {
                        VerticalGrid(modifier = Modifier.padding(vertical = 16.dp)) {
                            Tile(
                                text = stringResource(R.string.direct_chat),
                                image = directChat,
                                textColor = Color.White,
                                bgColor = Color(0xFFA53A14),
                            ) {
                                scope.launch {
                                    interstitialAd(
                                        activity = activity,
                                        interstitialAdManager = interstitialAdManager,
                                        interAdCounter = interAdCounter,
                                        doLast = {
                                            navController.push {
                                                DirectChatPage()
                                            }
                                        }
                                    )
                                }
                            }


                            Tile(
                                text = stringResource(R.string.btn_stylish_font),
                                image = directChat,
                                textColor = Color.White,
                                bgColor = Color(0xFFA53A14),
                            ) {
                                scope.launch {
                                    interstitialAd(
                                        activity = activity,
                                        interstitialAdManager = interstitialAdManager,
                                        interAdCounter = interAdCounter,
                                        doLast = {
                                            navController.push {
                                                DirectChatPage()
                                            }
                                        }
                                    )
                                }
                            }

                            Tile(
                                text = stringResource(R.string.top_bar_title),
                                image = statusSaver,
                                bgColor = Color(0xFF27A53C)
                            ) {
                                interstitialAd(
                                    activity, interstitialAdManager,
                                    interAdCounter = interAdCounter,
                                ) {
                                    navController.push {
                                        WhatsAppStatusPage()
                                    }
                                }
                            }

                            Tile(
                                text = stringResource(R.string.privacy_text),
                                image = privacy,
                                bgColor = Color(0xFF015786),
                            ) {
                                openPrivacyPolicyInWeb(activity)
                            }

                            Tile(
                                text = stringResource(R.string.share_text),
                                image = share,
                                bgColor = Color(0xFFA2094C),
                            ) {
                                shareThisApp(activity)
                            }
                        }
                    }
                }
            }
            BannerAdUnit(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    BackHandler {
        interstitialAd(
            activity = activity,
            interstitialAdManager = interstitialAdManager,
            interAdCounter = interAdCounter,
            doLast = {
                navController.pop()
            }
        )
    }
}


@Preview
@Composable
fun HomepagePreview() {
    WhatsappStatusSaverTheme {
        Homepage()
    }
}


@Composable
fun Tile(
    modifier: Modifier = Modifier,
    text: String,
    image: Painter,
    textColor: Color = Color.White,
    bgColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .size(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .padding(24.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = image, contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp)
                .shadow(elevation = 12.dp)
        )
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
    }
}


@Preview
@Composable
fun TilePreview() {
    WhatsappStatusSaverTheme {
        Tile(
            text = "Privacy Policy",
            image = painterResource(R.drawable.btn_privacy),
            textColor = Color.White,
            bgColor = Color.Blue,
        ) {}
    }
}
