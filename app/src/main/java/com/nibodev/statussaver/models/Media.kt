package com.nibodev.statussaver.models

import android.net.Uri
import androidx.compose.runtime.MutableState

abstract class Media {
    abstract val path: String
    abstract val isSaved: MutableState<Boolean>

    fun uri(): Uri = if (path.startsWith("content")) Uri.parse(path) else Uri.parse("file:$path")
}