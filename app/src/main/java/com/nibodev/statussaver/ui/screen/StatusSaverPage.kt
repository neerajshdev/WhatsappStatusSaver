package com.nibodev.statussaver.ui.screen

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import com.nibodev.statussaver.*
import com.nibodev.statussaver.R
import com.nibodev.statussaver.models.Media
import com.nibodev.statussaver.models.StatusImage
import com.nibodev.statussaver.models.StatusVideo
import com.nibodev.statussaver.ui.LocalNavController
import com.nibodev.statussaver.ui.components.*
import kotlinx.coroutines.launch
import com.nibodev.statussaver.ui.interstitialAdManager
import com.nibodev.statussaver.ui.statusSaverNativeAdManager


@Composable
fun StatusSaverPage(model: MainViewModel) {
    val navController = LocalNavController.current
    val scaffoldState = rememberScaffoldState()
    val topBarTitle = stringResource(id = R.string.top_bar_title)
    val activity = LocalContext.current as MainActivity

    BackHandler {
        interstitialAd(
            activity = activity,
            interstitialAdManager = interstitialAdManager,
            doLast = {
                navController.pop()
            }
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = topBarTitle,
                onShareClick = {
                    shareThisApp(activity)
                }
            )
        },
        content = { paddingValues ->
            OnBackgroundImage(
                painter = painterResource(R.drawable.bg),
                modifier = Modifier.fillMaxSize()
            ) {
                TabLayout(model = model, modifier = Modifier.padding(paddingValues))
            }
        }
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabLayout(model: MainViewModel, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = 2)
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Tabs(pagerState = pagerState)
        TabsContent(pagerState = pagerState,model = model, modifier = Modifier.weight(1f))
        BannerAdUnit()
    }

    ShowAdOnPagerStateChange(pagerState = pagerState)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShowAdOnPagerStateChange(pagerState: PagerState) {
    data class Prop(var value: Boolean)
    val isRecomposition = remember {Prop(false)}
    val activity = LocalContext.current as Activity
    LaunchedEffect(pagerState.currentPage) {
        console("Launched block run")
        if (isRecomposition.value)
        interstitialAd(
            activity, interstitialAdManager,
        )
        isRecomposition.value = true
    }

    SideEffect {
        console("side block run")
    }
}


@ExperimentalPagerApi
@Composable
private fun Tabs(pagerState: PagerState) {
    val tabsList =
        listOf(stringResource(R.string.recent_stories), stringResource(R.string.saved_stories))
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as Activity
    val adTime = remember { AdTimeHandler(10 * 1000) }
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        divider = {
            TabRowDefaults.Divider(
                thickness = 4.dp,
                color = Color.White
            )
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 4.dp,
                color = MaterialTheme.colors.primaryVariant
            )
        }
    ) {
        val colorOnSurface = MaterialTheme.colors.onSurface
        tabsList.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Text(
                        tabsList[index],
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Cursive,
                        color = if (pagerState.currentPage == index) colorOnSurface else colorOnSurface.copy(
                            alpha = 0.7f
                        )
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}


@ExperimentalPagerApi
@Composable
private fun TabsContent(modifier: Modifier = Modifier, pagerState: PagerState, model: MainViewModel) {
    HorizontalPager(state = pagerState, modifier = modifier) { page ->
        when (page) {
            0 -> FirstTabPage(model)
            1 -> SecondTabPage(model)
        }
    }
}

@Composable
private fun FirstTabPage(
    model: MainViewModel
) {
    val context = LocalContext.current
    val nc = LocalNavController.current

    // show the media files if the path to media is knows
    // else ask the user to select the media folder
    // this is all required for api level > 28
    if (model.mediaSelected.value) {
        val isModelInit = remember {
            mutableStateOf(false)
        }
        // init the model content only once
        if (!isModelInit.value) {
            model.initMedia()
            isModelInit.value = true
        }

        ScrollableRecentMediaList(
            media = model.recentMedia,
            onItemChosen = {
                when (it) {
                    is StatusImage -> nc.push { ImageScreen(image = it) }
                    is StatusVideo -> nc.push { VideoScreen(video = it) }
                }
            },
            onDownloadClick = { media ->
                model.saveFile(media.path, onSuccess = { str ->
                    media.isSaved.value = true
                    Toast.makeText(context, "File saved to: $str", Toast.LENGTH_SHORT).show()
                    loadInterstitialAd(context) { it.show(context as Activity) }
                })
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        MediaSelectDiaLog {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                model.selectMedia()
            }
        }
    }
}


@Composable
fun MediaSelectDiaLog(onMediaSelectClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "OOPs! Click on Locate and select the Media folder.",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )

            Button(onClick = onMediaSelectClick) {
                Text(
                    text = "Locate",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}


@Composable
private fun SecondTabPage(model: MainViewModel) {
    val nc = LocalNavController.current
    ScrollableSavedMediaList(
        media = model.savedMedia,
        onItemChosen = {
            when (it) {
                is StatusImage -> nc.push { ImageScreen(image = it) }
                is StatusVideo -> nc.push { VideoScreen(video = it) }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun RecentImageCard(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    isSaved: Boolean,
    onImageClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Box(modifier = modifier) {
        Image(
            bitmap = image, contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(320.dp)
                .clickable(onClick = onImageClick)
        )
        DownloadStrip(
            downloadDone = isSaved, onClick = { onDownloadClick() }, modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(320.dp)
        )
    }
}

@Composable
fun RecentVideoCard(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    isSaved: Boolean,
    label: String,
    onImageClick: () -> Unit,
    onDownloadClick: () -> Unit
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

        DownloadStrip(
            downloadDone = isSaved,
            onClick = { onDownloadClick() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(320.dp)
        )
    }
}


private val recentMediaScrollState = LazyListState()

@Composable
fun ScrollableRecentMediaList(
    media: List<Media>,
    modifier: Modifier = Modifier,
    onItemChosen: (Media) -> Unit,
    onDownloadClick: (Media) -> Unit,
) {
    LazyColumn(
        state = recentMediaScrollState,
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            NativeMediumAdUnit(
                nativeAdManager = statusSaverNativeAdManager
            )
        }
        items(media.size, key = { (media[it]).path }) {
            val mediaItem = media[it]
            val itemModifier = Modifier
                .padding(vertical = 12.dp)
                .padding(horizontal = 8.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium)

            when (mediaItem) {
                is StatusImage -> {
                    RecentImageCard(
                        image = mediaItem.image,
                        isSaved = mediaItem.isSaved.value,
                        onImageClick = { onItemChosen(mediaItem) },
                        onDownloadClick = {
                            onDownloadClick(mediaItem)
                        },
                        modifier = itemModifier
                    )
                }
                is StatusVideo -> {
                    RecentVideoCard(
                        image = mediaItem.thumbnail,
                        isSaved = mediaItem.isSaved.value,
                        label = mediaItem.duration,
                        onImageClick = { onItemChosen(mediaItem) },
                        onDownloadClick = {
                            onDownloadClick(mediaItem)
                        },
                        modifier = itemModifier
                    )
                }
            }

        }
    }
}


private val savedMediaScrollState = LazyListState()

@Composable
fun ScrollableSavedMediaList(
    media: List<Media>,
    modifier: Modifier = Modifier,
    onItemChosen: (Media) -> Unit,
) {
    LazyColumn(
        state = savedMediaScrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            NativeMediumAdUnit(
                nativeAdManager = statusSaverNativeAdManager,
            )
        }
        items(media.size, key = { (media[it]).path }) {
            val mediaItem = media[it]
            val itemModifier = Modifier
                .padding(vertical = 12.dp)
                .padding(horizontal = 8.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium)

            if (mediaItem is StatusImage) {
                SavedImageCard(
                    image = mediaItem.image,
                    onImageClick = { onItemChosen(mediaItem) },
                    modifier = itemModifier
                )
            } else if (mediaItem is StatusVideo) {
                SavedVideoCard(
                    image = mediaItem.thumbnail,
                    label = mediaItem.duration,
                    onImageClick = { onItemChosen(mediaItem) },
                    modifier = itemModifier
                )
            }
        }
    }
}