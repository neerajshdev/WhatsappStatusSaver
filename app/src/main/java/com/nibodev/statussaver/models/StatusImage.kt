package com.nibodev.statussaver.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap

class StatusImage(
    override val path: String,
    val image: ImageBitmap,
    override var isSaved: MutableState<Boolean> = mutableStateOf(false)
) : Media()