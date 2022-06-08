package com.nibodev.statussaver.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.scale
import androidx.documentfile.provider.DocumentFile
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.nibodev.statussaver.*
import com.nibodev.statussaver.R
import com.nibodev.statussaver.models.*
import com.nibodev.statussaver.navigation.LocalNavController
import com.nibodev.statussaver.ui.components.*
import com.nibodev.statussaver.ui.interAdCounter
import com.nibodev.statussaver.ui.interstitialAdManager
import com.nibodev.statussaver.ui.statusSaverNativeAdManager
import kotlinx.coroutines.launch
import java.io.File

val pagerChangeInterAdCounter by lazy {
    val clickThreshold = try {
        Firebase.remoteConfig.getString("status_tab_swipe_ad_threshold").toInt()
    } catch (ex: Exception) {
        3
    }
    AdCounter(clickThreshold)
}

@Composable
fun WhatsAppStatusPage() {
    val navController = LocalNavController.current
    val scaffoldState = rememberScaffoldState()
    val topBarTitle = stringResource(id = R.string.top_bar_title)
    val activity = LocalContext.current as MainActivity
    val viewModel = activity.viewModel

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
                TabLayout(model = viewModel, modifier = Modifier.padding(paddingValues))

                BackHandler {
                    interstitialAd(
                        activity = activity,
                        interAdCounter = interAdCounter,
                        interstitialAdManager = interstitialAdManager,
                        doLast = {
                            navController.pop()
                        }
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalPagerApi::class)
val pagerState = PagerState(pageCount = 2)

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabLayout(model: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Tabs(pagerState = pagerState)
        TabsContent(pagerState = pagerState, modifier = Modifier.weight(1f))
        BannerAdUnit()
    }

    ShowAdOnPagerStateChange(pagerState = pagerState)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShowAdOnPagerStateChange(pagerState: PagerState) {
    data class Prop(var value: Boolean)

    val isRecomposition = remember { Prop(false) }
    val activity = LocalContext.current as Activity

    LaunchedEffect(pagerState.currentPage) {
        if (isRecomposition.value) {
            interstitialAd(activity, interstitialAdManager, pagerChangeInterAdCounter)
        }
        isRecomposition.value = true
    }
}


@ExperimentalPagerApi
@Composable
private fun Tabs(pagerState: PagerState) {
    val tabsList =
        listOf(stringResource(R.string.recent_stories), stringResource(R.string.saved_stories))
    val scope = rememberCoroutineScope()
//    val context = LocalContext.current as Activity
//    val adTime = remember { AdTimeHandler(10 * 1000) }
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
private fun TabsContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
) {
    HorizontalPager(state = pagerState, modifier = modifier) { page ->
        when (page) {
            0 -> RecentMedia()
            1 -> SavedMedia()
        }
    }
}

val PermStateMap = mutableMapOf<String, MutableState<Boolean>>()


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun scopedStoragePermission(key: String): Boolean {
    val activity = LocalContext.current as MainActivity
    val whatsappLoc = "Android/media/com.whatsapp/WhatsApp/Media".replace("/", "%2F")
    var hasUriPermission by remember {
        PermStateMap[key] ?: mutableStateOf(hasUriPermission(activity)).also { PermStateMap[key] = it }
    }

    if (!hasUriPermission) {
        val activityLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { uri ->
                uri.data?.data?.let {
                    console(
                        "User chosen Uri tree:  $it ends with $whatsappLoc => ${
                            it.path?.endsWith(
                                whatsappLoc
                            )
                        }"
                    )
                    if (it.toString().endsWith("WhatsApp%2FMedia", true)) {
                        activity.contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        hasUriPermission = true
                    }
                }
            }
        )
        MediaSelectDiaLog {
            activityLauncher.launch(
                buildOpenUriTreeIntent(
                    activity, whatsappLoc
                )
            )
        }
    }

    return hasUriPermission
}


@Composable
fun hasStoragePermission(key: String): Boolean {
    val activity = LocalContext.current as MainActivity
    var hasStoragePermission by remember {
        PermStateMap[key] ?: mutableStateOf(hasStoragePermissions(activity)).also { PermStateMap[key] = it }
    }
    if (!hasStoragePermission) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { result ->
                hasStoragePermission =
                    result[Manifest.permission.READ_EXTERNAL_STORAGE] == true && result[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
            }
        )

        fun askStoragePermission() {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { askStoragePermission() }) {
                Text(text = "grant storage permissions")
            }
        }
    }
    return hasStoragePermission
}


@Composable
private fun RecentMedia() {
    val activity = LocalContext.current as MainActivity
    val viewModel = activity.viewModel
    val navController = LocalNavController.current

    // When the scoped storage is enable
    if (Build.VERSION.SDK_INT > 28) {
        if (scopedStoragePermission("scopedStoragePerm")) {
            // here we have the uri permission
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val uriTree = activity.contentResolver.persistedUriPermissions.first().uri.run {
                    DocumentFile.fromTreeUri(activity, this)?.findFile(".Statuses")
                }
                if (uriTree != null) {
                    viewModel.loadWhatsAppStatus(uriTree)
                }

                File(Environment.getExternalStorageDirectory().absolutePath + "/" + Environment.DIRECTORY_DCIM + "/StatusSaver").run {
                    if (!exists()) {
                        val created = mkdir()
                        console("is StatusSaver dir created = $created")
                    }
                }
            }

            ScrollableRecentMediaList(
                media = viewModel.recentMedia.value,
                onItemChosen = {
                    when (it) {
                        is ImageMedia -> navController.push { ImagePage(image = it) }
                        is VideoMedia -> navController.push { VideoPage(video = it) }
                    }
                },
                onDownloadClick = { media ->
                    val StatusSaverPath = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DCIM}/StatusSaver"
                    viewModel.download(Uri.parse(media.location), context.contentResolver, StatusSaverPath) {
                        Toast.makeText(activity, "saved to: $it", Toast.LENGTH_LONG).show()
                        activity.getExternalFilesDir(null)?.absolutePath?.let {
                            viewModel.loadSavedStatus(it)
                        }
                    }

                    interstitialAd(
                        activity = activity,
                        interAdCounter = interAdCounter,
                        interstitialAdManager = interstitialAdManager,
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }

    } else {
        // when the scoped storage is disable
        if (hasStoragePermission("storagePerm")) {
            // we have the storage permissions here
            LaunchedEffect(Unit) {
                val path =
                    Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"
                viewModel.loadWhatsAppStatus(path)

                File(Environment.getExternalStorageDirectory().absolutePath + "/" + Environment.DIRECTORY_DCIM + "/StatusSaver").run {
                    if (!exists()) {
                        val created = mkdir()
                        console("is StatusSaver dir created = $created")
                    }
                }
            }
            ScrollableRecentMediaList(
                media = viewModel.recentMedia.value,
                onItemChosen = { media ->
                    when (media) {
                        is ImageMedia -> navController.push { ImagePage(image = media) }
                        is VideoMedia -> navController.push { VideoPage(video = media) }
                    }
                },
                onDownloadClick = { media ->
                    val pathToSavedStatus = Environment.getExternalStorageDirectory().absolutePath + "/" + Environment.DIRECTORY_DCIM + "/StatusSaver"
                    viewModel.download(media.location, pathToSavedStatus) {
                        Toast.makeText(activity, "saved to: $it", Toast.LENGTH_LONG).show()
                        viewModel.loadSavedStatus(pathToSavedStatus)
                    }
                    interstitialAd(
                        activity = activity,
                        interAdCounter = interAdCounter,
                        interstitialAdManager = interstitialAdManager,
                    )
                }
            )
        }
    }
}

fun hasUriPermission(context: Context): Boolean =
    context.contentResolver.persistedUriPermissions.size > 0

fun hasStoragePermissions(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
}


@Composable
fun MediaSelectDiaLog(onMediaSelectClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .shadow(elevation = 6.dp)
                .background(color = MaterialTheme.colors.background)
        ) {
            Text(
                text = stringResource(R.string.uri_permission_dialog_text),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(16.dp)
            )

            TextButton(onClick = onMediaSelectClick, modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 8.dp, end = 16.dp)) {
                Text(
                    text = "OPEN",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}


@Composable
private fun SavedMedia() {
    val activity = LocalContext.current as MainActivity
    val viewModel = activity.viewModel
    val navController = LocalNavController.current

    // When the scoped storage is enable
    if (Build.VERSION.SDK_INT > 28) {
        if (scopedStoragePermission("scopedStoragePerm")) {
            LaunchedEffect(Unit) {
                activity.getExternalFilesDir(null)?.absolutePath?.let {
                    viewModel.loadSavedStatus(it)
                }
            }

            ScrollableSavedMediaList(
                media = viewModel.savedMedia.value,
                onItemChosen = { media ->
                    when (media) {
                        is ImageMedia -> navController.push { ImagePage(image = media) }
                        is VideoMedia -> navController.push { VideoPage(video = media) }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

    } else {
        // when the scoped storage is disable
        if (hasStoragePermission("storagePerm")) {
            // we have the storage permissions here
            LaunchedEffect(Unit) {
                val pathToSavedStatus = Environment.getExternalStorageDirectory().absolutePath + "/${Environment.DIRECTORY_DCIM}/StatusSaver"
                viewModel.loadSavedStatus(pathToSavedStatus)
            }
            ScrollableSavedMediaList(
                media = viewModel.savedMedia.value,
                onItemChosen = { media ->
                    when (media) {
                        is ImageMedia -> navController.push { ImagePage(image = media) }
                        is VideoMedia -> navController.push { VideoPage(video = media) }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
fun RecentImageCard(
    modifier: Modifier = Modifier,
    image: ImageMedia,
    isSaved: Boolean = false,
    onImageClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Box(modifier = modifier) {
        image.Draw(
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
    videoMedia: VideoMedia,
    isSaved: Boolean = false,
    onImageClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    var videoLength by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit ) {
        getVideoMetaData(context, videoMedia.location)?.use {
            try {
                videoLength =
                    it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.run { formatTime(this.toLong()) }
                        .toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    Box(modifier = modifier) {
        videoMedia.Draw(
            modifier = Modifier
                .size(320.dp)
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
            text = videoLength,
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


@Composable
fun ScrollableRecentMediaList(
    media: List<Media>,
    modifier: Modifier = Modifier,
    onItemChosen: (Media) -> Unit,
    onDownloadClick: (Media) -> Unit,
) {
    LazyColumn(
        state = rememberForeverLazyListState(key = "recentMediaList"),
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            NativeMediumAdUnit(
                nativeAdManager = statusSaverNativeAdManager
            )
        }
        items(media.size, key = { ix -> (media[ix]).location }) { index ->
            val mediaItem = media[index]
            val itemModifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium)

            when (mediaItem) {
                is ImageMedia -> {
                    RecentImageCard(
                        image = mediaItem,
                        onImageClick = { onItemChosen(mediaItem) },
                        onDownloadClick = {
                            onDownloadClick(mediaItem)
                        },
                        modifier = itemModifier
                    )
                }
                is VideoMedia -> {
                    RecentVideoCard(
                        videoMedia = mediaItem,
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


@Composable
fun ScrollableSavedMediaList(
    media: List<Media>,
    modifier: Modifier = Modifier,
    onItemChosen: (Media) -> Unit,
) {
    LazyColumn(
        state = rememberForeverLazyListState(key = "savedMediaList"),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            NativeMediumAdUnit(
                nativeAdManager = statusSaverNativeAdManager,
            )
        }
        items(media.size, key = { (media[it]).location }) { index ->
            val mediaItem = media[index]
            val itemModifier = Modifier
                .size(320.dp)
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium)

            when (mediaItem) {
                is ImageMedia -> mediaItem.Draw(modifier = itemModifier.clickable(onClick = {
                    onItemChosen(
                        mediaItem
                    )
                }))
                is VideoMedia -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = itemModifier.clickable(onClick = {
                        onItemChosen(
                            mediaItem
                        )
                    })
                ) {
                    mediaItem.Draw()
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_play_circle_outline_24),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                }
            }
        }
    }
}


/**
 * private field contains all scroll values
 */

private val SaveMap = mutableMapOf<String, KeyParams>()

private data class KeyParams(
    val params: String = "",
    val index: Int,
    val scrollOffset: Int
)

/**
 * Save scroll state on all time
 * @param key value for comparing screen
 * @param params arguments for find different between screen
 * @param initialFirstVisibleItemIndex see [LazyListState.firstVisibleItemIndex]
 * @param initialFirstVisibleItemScrollOffset see [LazyListState.firstVisibleItemScrollOffset]
 */
@Composable
fun rememberForeverLazyListState(
    key: String,
    params: String = "",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        var savedValue = SaveMap[key]
        val savedIndex = savedValue?.index ?: initialFirstVisibleItemIndex
        val savedScrollOffset = savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        LazyListState(savedIndex, savedScrollOffset)
    }

    DisposableEffect(Unit) {
        onDispose {
            val index = scrollState.firstVisibleItemIndex
            val offset = scrollState.firstVisibleItemScrollOffset
            SaveMap[key] = KeyParams(params, index, offset)
        }
    }
    return scrollState
}