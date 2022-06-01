package com.nibodev.statussaver.ui.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.nibodev.statussaver.*
import com.nibodev.statussaver.R
import com.nibodev.statussaver.ui.LocalNavController
import com.nibodev.statussaver.ui.components.*
import com.nibodev.statussaver.ui.homeNativeAdManager
import com.nibodev.statussaver.ui.interAdCounter
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import kotlinx.coroutines.launch
import com.nibodev.statussaver.ui.interstitialAdManager

@Composable
fun HomePage() {
    val nc = LocalNavController.current
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as Activity
    val viewModel = (LocalContext.current as MainActivity).viewModel
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                NativeSmallAdUnit(nativeAdManager = homeNativeAdManager)

                Spacer(modifier = Modifier.weight(1f))

                VerticalGrid(modifier = Modifier) {
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
                                    nc.push {
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
                            nc.push {
                                StatusSaverPage(model = viewModel)
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

                Spacer(modifier = Modifier.weight(1f))
                // banner ad view
                BannerAdUnit()
            }
        }
    }

    BackHandler {
        interstitialAd(
            activity = activity,
            interstitialAdManager = interstitialAdManager,
            interAdCounter = interAdCounter,
            doLast = {
                nc.pop()
            }
        )
    }
}


@Preview
@Composable
fun HomepagePreview() {
    WhatsappStatusSaverTheme {
        HomePage()
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
        Image(
            painter = image, contentDescription = null,
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
                .size(56.dp)
                .shadow(elevation = 12.dp)
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
