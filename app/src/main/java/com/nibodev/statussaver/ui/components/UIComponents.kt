package com.nibodev.statussaver.ui.components

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import com.nibodev.statussaver.NativeAdManager
import com.nibodev.statussaver.R
import com.nibodev.statussaver.console
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import kotlin.math.ceil


/**
 * Custom Vertical grid implementation
 * @param rowSize: number of max items in a row
 * @param cellSpacing: space between each item vertically and horizontally
 * @param content: define your child content for this grid layout
 */
@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    rowSize: Int = 2,
    cellSpacing: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = { measurebles, constraints ->
            val itemSize = (constraints.maxWidth - cellSpacing.roundToPx()).toFloat() / rowSize
            val placeables =
                measurebles.map {
                    it.measure(
                        constraints.copy(
                            maxWidth = itemSize.toInt(),
                            maxHeight = itemSize.toInt()
                        )
                    )
                }

            val rows = (placeables.size / rowSize.toFloat()).also { ceil(it).toInt() }
            val totalHeight = itemSize * rows + (rows - 1) * cellSpacing.roundToPx()

            layout(constraints.maxWidth, totalHeight.toInt()) {
                var c = 0
                var r = 0
                for (i in placeables.indices) {
                    val x: Int = ((itemSize + cellSpacing.toPx()) * c).toInt()
                    val y: Int = ((itemSize + cellSpacing.toPx()) * r).toInt()

                    // place the item
                    placeables[i].placeRelative(x, y)

                    // Fill the next row when the current get full
                    c++
                    if (c == rowSize) {
                        c = 0
                        r++
                    }
                }
            }
        }
    )
}


@Composable
fun DownloadStrip(
    downloadDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val id = if (downloadDone) R.drawable.ic_round_download_done_24 else
        R.drawable.ic_round_download_24

    val itemModifier = modifier
        .width(320.dp)
        .background(color = MaterialTheme.colors.primary)

    IconButton(onClick = { if (!downloadDone) onClick() }, modifier = itemModifier) {
        Icon(
            painter = painterResource(id),
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary,
        )
    }
}


@Composable
fun TopAppBar(onShareClick: () -> Unit = {}, title: String) {
    androidx.compose.material.TopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.h6)
        },
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_share_24),
                    contentDescription = "Share this app",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}


@Preview
@Composable
private fun TopBarPrev() {
    WhatsappStatusSaverTheme {
        TopAppBar({}, "WA saver")
    }
}


@Composable
fun NativeSmallAdUnit(
    modifier: Modifier = Modifier,
    nativeAdManager: NativeAdManager,
) {
    console("NativeAdUnit Function")
    var nativeAd by remember {
        mutableStateOf<NativeAd?>(null)
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        console("NativeAdUnit Launched effect run")
        nativeAd = nativeAdManager.getAd(context)
        console("native ad loaded = ${nativeAd != null}")
    }

    AnimatedVisibility(visible = nativeAd != null) {
        console("AnimatedVisibility Function")
        val background = MaterialTheme.colors.background.toArgb()
        Box(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .size(360.dp)
        ) {
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context).inflate(R.layout.native_ad_small_template, null)
                },
                update = {
                    console("updating native ad view")
                    try {
                        val colorDrawable = ColorDrawable(background)
                        val templateStyle = NativeTemplateStyle.Builder()
                            .withMainBackgroundColor(colorDrawable).build()
                        with(it.findViewById<TemplateView>(R.id.my_template)) {
                            setStyles(templateStyle)
                            setNativeAd(nativeAd)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun NativeMediumAdUnit(
    modifier: Modifier = Modifier,
    nativeAdManager: NativeAdManager,
) {
    console("NativeAdUnit Function")
    var nativeAd by remember {
        mutableStateOf<NativeAd?>(null)
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        console("NativeAdUnit Launched effect run")
        nativeAd = nativeAdManager.getAd(context)
        console("native ad loaded = ${nativeAd != null}")
    }

    AnimatedVisibility(visible = nativeAd != null) {
        console("AnimatedVisibility Function")
        val background = MaterialTheme.colors.background.toArgb()
        Box(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .size(360.dp)
        ) {
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context).inflate(R.layout.native_ad_template_medium, null)
                },
                update = {
                    console("updating native ad view")
                    try {
                        val colorDrawable = ColorDrawable(background)
                        val templateStyle = NativeTemplateStyle.Builder()
                            .withMainBackgroundColor(colorDrawable).build()
                        with(it.findViewById<TemplateView>(R.id.my_template)) {
                            setStyles(templateStyle)
                            setNativeAd(nativeAd)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
fun OnBackgroundImage(
    modifier: Modifier = Modifier,
    painter: Painter,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}

@Composable
fun SavedImageCard(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    onImageClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Image(
            bitmap = image, contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(320.dp)
                .clickable(onClick = onImageClick)
        )
    }
}


@Composable
fun SavedVideoCard(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    label: String,
    onImageClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Image(
            bitmap = image, contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(320.dp)   // 2 x 2 square inch
                .clickable(onClick = onImageClick)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_round_play_circle_outline_24),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
        )

        Text(
            text = label,
            color = Color.White,
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp)
        )
    }
}

