package com.nibodev.statussaverforwhatsapp.models

import androidx.compose.runtime.MutableState

abstract class Media {
    abstract val path: String
    abstract val isSaved: MutableState<Boolean>
}