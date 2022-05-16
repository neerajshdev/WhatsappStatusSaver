package com.nibodev.composeapp

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nibodev.composeapp.model.Media
import com.nibodev.layer.data.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias dImage = com.nibodev.layer.data.Media.Image
typealias dVideo = com.nibodev.layer.data.Media.Video

class MainViewModel : ViewModel() {
    lateinit var repo: Repo

    private val _mediaList: MutableLiveData<List<Media>> = MutableLiveData()
    val mediaList: LiveData<List<Media>> = _mediaList

    private var dataLoaded = false

    /**
     * Loads only once
     */
    fun loadMedia() {
        if (dataLoaded) return
        viewModelScope.launch {
            val list = mutableListOf<Media>()
            withContext(Dispatchers.IO) {
                repo.loadMedia().collect() { media ->
                    val model = when(media) {
                        is dImage -> Media.Image(media.preview.asImageBitmap(), media.uri)
                        is dVideo -> Media.Video(media.preview.asImageBitmap(), media.uri)
                    }
                    list.add(model)
                }
                dataLoaded = true
            }
            _mediaList.value = list
        }
    }
}