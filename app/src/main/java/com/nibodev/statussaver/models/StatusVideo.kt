package com.nibodev.statussaver.models

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap

class StatusVideo(
    override val path: String,
    val thumbnail: ImageBitmap,
    val duration: String,
    override val isSaved: MutableState<Boolean>
) : Media()