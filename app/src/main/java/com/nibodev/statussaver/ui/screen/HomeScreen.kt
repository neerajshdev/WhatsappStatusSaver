package com.nibodev.statussaver.ui.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nibodev.statussaver.*
import com.nibodev.statussaver.R
import com.nibodev.statussaver.ui.LocalNavController
import com.nibodev.statussaver.ui.components.*
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import kotlinx.coroutines.launch

private val nativeAdManager = NativeAdManager("ca-app-pub-3940256099942544/2247696110", 3)
private val interstitialAdManager =
    InterstitialAdManager("ca-app-pub-3940256099942544/1033173712", 5)

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
        topBar = { TopAppBar(title = stringResource(R.string.top_bar_title))}
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
                    .padding(horizontal = 16.dp)
            ) {
                NativeSmallAdUnit(nativeAdManager = nativeAdManager)
                Spacer(modifier = Modifier.weight(1f))
                VerticalGrid(
                    modifier = Modifier
                ) {
                    val imageModifier = Modifier
                        .fillMaxSize()
                        .shadow(elevation = 8.dp)
                    Image(
                        painter = directChat,
                        contentDescription = "direct chat",
                        modifier = imageModifier.clickable {
                           scope.launch {
                               if (isNetworkConnected(activity)) {
                                   interstitialAd(
                                       activity, interstitialAdManager,
                                       onAdDismissedFsc = {
                                           nc.push {
                                              DirectChatPage()
                                           }
                                       }
                                   )
                               } else {
                                   nc.push {
                                       DirectChatPage()
                                   }
                               }
                           }
                        }
                    )
                    Image(
                        painter = statusSaver,
                        contentDescription = "status saver",
                        modifier = imageModifier.clickable {
                            scope.launch {
                                if (isNetworkConnected(activity)) {
                                    interstitialAd(
                                        activity, interstitialAdManager,
                                        onAdDismissedFsc = {
                                            nc.push {
                                                RecentStoriesScreen(model = viewModel)
                                            }
                                        }
                                    )
                                } else {
                                    nc.push {
                                        RecentStoriesScreen(model = viewModel)
                                    }
                                }
                            }
                        }
                    )
                    Image(
                        painter = privacy,
                        contentDescription = "privacy policy",
                        modifier = imageModifier.clickable {}
                    )
                    Image(
                        painter = share,
                        contentDescription = "share this app",
                        modifier = imageModifier.clickable {
                            shareThisApp(activity)
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                // banner ad view
                BannerAdUnit()
            }
        }

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
        icon: Painter,
        text: String,
        bgColor: Color,
        textColor: Color = bgColor,
        iconTint: Color = bgColor,
    ) {
        val gradient = Brush.verticalGradient(
            0.055f to Color.White,
            1f to bgColor
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .background(brush = gradient, shape = RoundedCornerShape(24.dp))
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(96.dp)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(36.dp))
                    .background(color = Color.White)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.h5,
                    color = textColor,
                )
            }
        }
    }


    @Preview
    @Composable
    fun TilePreview() {
        WhatsappStatusSaverTheme {
            Tile(
                text = "Status Saver",
                icon = painterResource(id = R.drawable.ic_baseline_file_download_24),
                bgColor = Color(0xffA16AE8),
                modifier = Modifier.size(360.dp)
            )
        }
    }
