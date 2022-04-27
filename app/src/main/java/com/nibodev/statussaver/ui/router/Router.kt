package com.nibodev.statussaver.ui.router

import androidx.compose.runtime.mutableStateOf

enum class ScreenType {
    HOME_SCREEN,
    SCREEN_VIDEO,
    SCREEN_IMAGE
}




object Screen {
    // initially current screen will be the home screen
    val current = mutableStateOf(ScreenType.HOME_SCREEN);
    
    fun setScreen(screen: ScreenType ) {
        current.value = screen
    }
}