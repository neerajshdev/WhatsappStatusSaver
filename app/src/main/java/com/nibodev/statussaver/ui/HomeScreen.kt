package com.nibodev.statussaver.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.nibodev.statussaver.MainActivity
import com.nibodev.statussaver.MainViewModel
import com.nibodev.statussaver.R
import com.nibodev.statussaver.loadInterstitialAd
import com.nibodev.statussaver.ui.components.TopBar
import com.nibodev.statussaver.ui.layout.*
import com.nibodev.statussaver.ui.router.Screen
import com.nibodev.statussaver.ui.router.ScreenType

enum class Show {
    RECENT_IMAGE_LIST,
    RECENT_VIDEO_LIST,
    SAVED_IMAGE_LIST,
    SAVED_VIDEO_LIST,
    HOME_CONTENT
}

var showWhat by mutableStateOf(Show.HOME_CONTENT)


@Composable
fun HomeScreen(model: MainViewModel) {
    val scaffoldState = rememberScaffoldState()
    val topBarTitle = stringResource(id = R.string.top_bar_title)

    val activity = LocalContext.current as MainActivity

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(title = topBarTitle, onShareClick = {
                val myIntent = Intent(Intent.ACTION_SEND)
                myIntent.type = "text/plain"
                val body = Firebase.remoteConfig.getString("share_text_body")
                val sub = Firebase.remoteConfig.getString("share_sub_body")
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub)
                myIntent.putExtra(Intent.EXTRA_TEXT,body)
                activity.startActivity(Intent.createChooser(myIntent, "Share Using"))
            })
        },
        content = { HomeScreenContent(model) }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreenContent(model: MainViewModel) {
    val context = LocalContext.current
    when (showWhat) {
        Show.RECENT_IMAGE_LIST ->
            ScrollableRecentImageList(
                model.statusImage,
                onItemChosen = {
                    model.imageEntry = it
                    Screen.setScreen(ScreenType.SCREEN_IMAGE)
                },
                onDownloadClick = { statusImage ->
                    model.saveFile(src = statusImage.path, onSuccess = { path ->
                        statusImage.isSaved.value = true
                        Toast.makeText(context, "File saved to: $path", Toast.LENGTH_SHORT).show()
                        loadInterstitialAd(context){it.show(context as Activity)}
                    })
                }
            )
        Show.RECENT_VIDEO_LIST ->
            ScrollableRecentVideoList(
                model.statusVideo,
                onItemChosen = {
                    model.videoEntry = it
                    Screen.setScreen(ScreenType.SCREEN_VIDEO)
                },
                onDownloadClick = { statusVideo ->
                    model.saveFile(
                        src = statusVideo.path,
                        onSuccess = { it ->
                            statusVideo.isSaved.value = true
                            Toast.makeText(context, "File saved to: $it", Toast.LENGTH_SHORT).show()
                            loadInterstitialAd(context){it.show(context as Activity)}
                        }
                    )
                }
            )

        Show.SAVED_IMAGE_LIST ->
            ScrollableSavedImageList(images = model.savedImage, onItemChosen = {
                model.imageEntry = it
                Screen.setScreen(ScreenType.SCREEN_VIDEO)
            })
        Show.SAVED_VIDEO_LIST ->
            ScrollableSavedVideoList(videos = model.savedVideo, onItemChosen = {
                model.videoEntry = it
                Screen.setScreen(ScreenType.SCREEN_VIDEO)
            })

        Show.HOME_CONTENT -> {
            TabScreen(model)
        }
    }
}


