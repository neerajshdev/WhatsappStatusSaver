package com.ns.whatsappstatussaver.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap

class StatusVideo(
    filepath: String,
    val thumbnail: ImageBitmap,
    val duration: String,
    _isSaved: Boolean = false
) : Media() {
    override var isSaved : MutableState<Boolean> = mutableStateOf(_isSaved)
    override val path: String = filepath
}