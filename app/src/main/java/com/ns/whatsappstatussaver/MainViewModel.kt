package com.ns.whatsappstatussaver

import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.whatsappstatussaver.models.Media
import com.ns.whatsappstatussaver.models.StatusImage
import com.ns.whatsappstatussaver.models.StatusVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val WHATSAPP_STATUS = "/WhatsApp/Media/.Statuses/"

class MainViewModel : ViewModel() {

    private val repository = Systems.getRepo()

    private var mediaFiles = listOf<String>()
    private var savedMediaFiles = listOf<String>()
    private val savedMediaMap = mutableMapOf<String, Boolean>()

    // ui data
    val statusVideo = mutableStateListOf<StatusVideo>()
    val statusImage = mutableStateListOf<StatusImage>()
    val savedImage = mutableStateListOf<StatusImage>()
    val savedVideo = mutableStateListOf<StatusVideo>()
    val recentMedia = mutableStateListOf<Media>()
    val savedMedia = mutableStateListOf<Media>()

    // input data for video and image screen
    var videoEntry: StatusVideo? = null
    var imageEntry: StatusImage? = null

    fun init() {
        viewModelScope.launch(Dispatchers.Default) {
            updateMediaFiles()
            updateMap()

            updateMedia()

            // Saved Media
            updateSavedMedia()
        }
    }

    private fun updateMediaFiles() {
        mediaFiles = repository.getListFiles(File(whatsappMedia()))
        savedMediaFiles = repository.getListFiles(File(savedFilePath()))
    }

    private fun updateMap() {
        for (file in mediaFiles) {
            val name = File(file).name
            savedMediaMap[name] = false
        }

        for (file in savedMediaFiles) {
            val name = File(file).name
            savedMediaMap[name] = true
        }
    }


//    // called when user do a refresh
//    private fun refresh() {
//        updateMediaFiles()
//        updateMap()
//        updateMedia()
//        updateSavedMedia()
//    }

    private fun whatsappMedia(): String {
        // todo("Update the code because it uses the deprecated function")
        return Environment.getExternalStorageDirectory().toString() + WHATSAPP_STATUS
    }

    fun saveFile(src: File, onSuccess: (String) -> Unit) {
        val name = src.name
        val dest = File(savedFilePath() + File.separator + name)
        Log.d(TAG, "saveFile: dest = ${dest.absolutePath}")
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveFile(src, dest)
            }
            onSuccess.invoke(dest.absolutePath)
            withContext(Dispatchers.Default) {
                savedMediaFiles = repository.getListFiles(File(savedFilePath()))
                updateSavedMedia()
            }
        }
    }

    private fun savedFilePath(): String {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (!path.exists()) path.mkdir()
        return path.absolutePath + File.separator + "StatusSaver"
    }


    private fun videoFiles(): List<String> {
        return mediaFiles.filter { it.endsWith(".mp4") }
    }

    private fun imageFiles(): List<String> {
        return mediaFiles.filter { it.endsWith(".jpg") }
    }


    private fun updateSavedMedia() {
        savedVideo.clear()
        savedImage.clear()
        savedMedia.clear()
        val savedVideoFiles = savedMediaFiles.filter { it.endsWith(".mp4") }
        repository.loadStatusVideo(savedVideoFiles, savedVideo, savedMediaMap)

        val savedImageFiles = savedMediaFiles.filter { it.endsWith(".jpg") }
        repository.loadStatusImage(savedImageFiles, savedImage, savedMediaMap)

        savedMedia.addAll(savedImage)
        savedMedia.addAll(savedVideo)
        savedMedia.shuffle()
    }


    private fun updateMedia() {
        statusVideo.clear()
        statusImage.clear()
        recentMedia.clear()
        val videoFiles = videoFiles()
        repository.loadStatusVideo(videoFiles, statusVideo, savedMediaMap)

        val imgFiles = imageFiles()
        repository.loadStatusImage(imgFiles, statusImage, savedMediaMap)
        recentMedia.addAll(statusVideo)
        recentMedia.addAll(statusImage)
        recentMedia.shuffle()
    }
}