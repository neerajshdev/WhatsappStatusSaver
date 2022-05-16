package com.nibodev.layer.data

import kotlinx.coroutines.flow.Flow

interface Repo {
    fun loadMedia() : Flow<Media>
    fun download(media: Media)
}