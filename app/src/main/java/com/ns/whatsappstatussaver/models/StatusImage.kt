package com.ns.whatsappstatussaver.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap

class StatusImage (
    filepath : String,
    val image: ImageBitmap,
    override var isSaved: MutableState<Boolean> = mutableStateOf(false)
) : Media() {
    override val path: String = filepath
}