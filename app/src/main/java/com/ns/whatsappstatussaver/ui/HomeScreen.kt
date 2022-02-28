package com.ns.whatsappstatussaver.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.ns.whatsappstatussaver.MainActivity
import com.ns.whatsappstatussaver.MainViewModel
import com.ns.whatsappstatussaver.ui.components.AppDrawer
import com.ns.whatsappstatussaver.ui.components.TopBar
import com.ns.whatsappstatussaver.ui.layout.*
import com.ns.whatsappstatussaver.ui.router.Screen
import com.ns.whatsappstatussaver.ui.router.ScreenType
import kotlinx.coroutines.launch
import java.io.File
import com.ns.whatsappstatussaver.loadInterstitialAd

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
    val scope = rememberCoroutineScope()
    val topBarTitle = when (showWhat) {
        Show.HOME_CONTENT -> "Home"
        Show.RECENT_VIDEO_LIST -> "Recent Video"
        Show.RECENT_IMAGE_LIST -> "Recent Image"
        Show.SAVED_VIDEO_LIST -> "Saved Video"
        Show.SAVED_IMAGE_LIST -> "Saved Image"
    }

    val openDrawer = {
        scope.launch {
            scaffoldState.drawerState.open()
        }
    }

    val closeDrawer = {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }

    val activity = LocalContext.current as MainActivity

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = { AppDrawer { closeDrawer(); showWhat = it } },
        topBar = {
            TopBar(onMenuClick = { openDrawer.invoke() }, title = topBarTitle, onShareClick = {
                val myIntent = Intent(Intent.ACTION_SEND)
                myIntent.setType("text/plain")
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
                    model.saveFile(src = File(statusImage.path), onSuccess = {
                        statusImage.isSaved.value = true
                        Toast.makeText(context, "File saved to: $it", Toast.LENGTH_SHORT).show()
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
                        src = File(statusVideo.path),
                        onSuccess = {
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


