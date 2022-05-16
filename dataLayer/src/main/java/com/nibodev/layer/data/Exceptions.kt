package com.nibodev.layer.data

import java.lang.RuntimeException

sealed class Exceptions(msg: String) : RuntimeException(msg) {
    object RequiredPersistedMediaUri : Exceptions("Media Uri is not persisted")
    object ReadExternalStorage : Exceptions("External storage permission is required")
    object MediaStateNotMounted : Exceptions("Currently media is not mounted")
}