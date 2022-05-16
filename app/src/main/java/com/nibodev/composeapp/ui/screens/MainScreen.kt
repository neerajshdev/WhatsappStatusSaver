package com.nibodev.composeapp.ui.screens

import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nibodev.composeapp.MainActivity
import com.nibodev.composeapp.PermissionManager
import com.nibodev.composeapp.d
import com.nibodev.composeapp.model.Media
import com.nibodev.composeapp.ui.CircularButton
import com.nibodev.composeapp.ui.MediaView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun content() : Int {
    var case  = 0
    if (PermissionManager.ifNeedToAskDocumentUri()) {
        case = 1
    } else if (PermissionManager.ifNeedToAskStoragePermission()) {
        case = 2
    }

    return case
}

@Composable
fun MainScreen(navController: NavController) {
    var content by remember { mutableStateOf(content())}

    when (content) {
        1 -> AskDocumentUri { content = 0 }
        2 -> AskStoragePermission { content = 0 }
        else -> MainContent()
    }
}


@Composable
private fun MainContent() {
    d("mainContent()")
    val activity = LocalContext.current as MainActivity
    val state = rememberLazyListState()
//    var topAppBarHidden by remember { mutableStateOf(false) }
//    val fraction = remember { Animatable(1f) }
//    val scope = rememberCoroutineScope()
//    var dragged = 0f
//    val scrollableState = rememberScrollableState(consumeScrollDelta = { delta ->
//        dragged += delta
//        if (dragged < -200f) {
//            dragged = -200f
//            if (topAppBarHidden.not()) {
//                topAppBarHidden = true
//                scope.launch {
//                    fraction.animateTo(0f)
//                }
//            }
//        } else if (dragged > 200f) {
//            dragged = 200f
//            if (topAppBarHidden) {
//                topAppBarHidden = false
//                scope.launch {
//                    fraction.animateTo(1f)
//                }
//            }
//        }
//        delta
//    })


    fun progressProvider(): Float {
        return 1f
    }


    activity.model.loadMedia()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    progressProvider = ::progressProvider,
                )
            },
            content = {
                val mediaList by activity.model.mediaList.observeAsState(listOf())
                MediaContainer(
                    mediaListProvider = { mediaList },
                    onItemClick = ::openContent,
                    lazyListState = state,
                    animProgressProvider = ::progressProvider
                )
            }
        )
    }
}


/**
 * Open the passed media content in
 * another screen.
 */
private fun openContent(media: Media) {
    when (media) {
        is Media.Image -> {}
        is Media.Video -> {}
    }
}


@Composable
private fun TopAppBar(
    modifier: Modifier = Modifier,
    progressProvider: () -> Float,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val layoutHeight = (placeable.height * progressProvider()).toInt()
                val layoutWidth = placeable.width
                layout(layoutWidth, layoutHeight) {
                    placeable.placeRelative(0, layoutHeight - placeable.height)
                }
            }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        CircularButton(icon = Icons.Rounded.Menu) {/*todo: on Menu click*/ }

        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f, true), contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "GbSaver",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            )
        }

        CircularButton(
            icon = Icons.Rounded.Share,
            color = MaterialTheme.colors.secondary
        ) { /*todo: onClick share button */ }
    }
}

@Composable
private fun MediaContainer(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    mediaListProvider: () -> List<Media>,
    onItemClick: (item: Media) -> Unit,
    lazyListState: LazyListState,
    animProgressProvider: () -> Float
) {
    Box(
        modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(
                    topStart = 32.dp * with(animProgressProvider()) { if (this < 0) 0f else this },
                    topEnd = 32.dp * with(animProgressProvider()) { if (this < 0) 0f else this }
                )
            )
            .padding(all = 32.dp),
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = contentModifier
        ) {
            items(items = mediaListProvider(), key = { item: Media -> item.uri }) { media: Media ->
                MediaView(
                    media = media,
                    onDownloadClick = {/* todo: On item download click*/ },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable { onItemClick(media) }
                )
            }
        }

        ScrollToTopButton()
    }
}

@Composable
private fun ScrollToTopButton() {

}


@Composable
private fun AskDocumentUri(onGrant: () -> Unit) {
    val scope = rememberCoroutineScope()
    fun request() {
        scope.launch {
            d("requested for Uri")
            if (PermissionManager.askForDocumentUri()) {
                d("Uri permission granted")
                onGrant()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(text = "add Whats folder!", style = MaterialTheme.typography.h4)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = ::request) {
                Text("Proceed", style = MaterialTheme.typography.button)
            }
        }
    }
}


@Composable
private fun AskStoragePermission(onGrant: () -> Unit) {
    val scope = rememberCoroutineScope()
    fun request() {
        scope.launch {
            if (PermissionManager.askForStoragePermission()) {
                onGrant()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Grant Storage permissions!",
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = ::request) {
                Text("grant", style = MaterialTheme.typography.button)
            }
        }
    }
}


private class FloatAnimator(private val scope: CoroutineScope) {
    private var isAnimating: Boolean = false

    fun downToZero(newValue: (Float) -> Unit) {
        animate(1f, 0f, newValue)
    }

    fun goUpToOne(newValue: (Float) -> Unit) {
        animate(0f, 1f, newValue)
    }


    fun isAnimationInProgress(): Boolean = isAnimating

    private fun animate(from: Float, to: Float, newValue: (Float) -> Unit) {
        d("FloatAnimator.animate()")
        d("Launching coroutine")
        scope.launch {
            isAnimating = true
            animate(
                initialValue = from,
                targetValue = to,
                block = { value, _ ->
                    newValue(value)
                }
            )
            isAnimating = false
        }
    }
}