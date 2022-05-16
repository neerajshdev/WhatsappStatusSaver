package com.nibodev.layer.data

import android.os.Environment
import androidx.core.net.toUri
import getFilesInFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import loadFrame
import loadImage
import java.io.File


/**
 * Usage of this class required
 * Read external storage and Write external storage permission
 */
class RepoImplUntilSdk28(private val mediaPath: String) : Repo {
    sealed class MediaException(val msg: String) : Exception(msg) {
        object MediaNotFoundException : MediaException("Could not found whatsapp media")
        object MediaMountException : MediaException("Media is not mount")
    }


    /**
     * @author:  Neeraj Sharma
     * @exception: throws MediaNotFoundException when whatsapp dir does not exists
     * and MediaMountException when media is not mounted
     *
     * @return : location directory wrapped in a File
     */
    private fun mediaLocation(): File {
        // todo: Think again about throwing this exception
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            throw MediaException.MediaMountException
        }

        val location = File(mediaPath)
        if (location.exists()) return location
        throw MediaException.MediaNotFoundException
    }


    /**
     * @exception Exceptions.ReadExternalStorage
     */
    override fun loadMedia(): Flow<Media> {
        return getFilesInFlow(mediaLocation())
            .filter {
                isMediaType(it)
            }
            .map {
                mediaFrom(it)
            }
    }


    private fun isMediaType(it: File): Boolean {
        return when (it.extension) {
            "jpg" -> true
            "jpeg" -> true
            "png" -> true
            "mp4" -> true
            "mp3" -> true
            else -> false
        }
    }

    override fun download(media: Media) {
        TODO("Not yet implemented")
    }


    private fun mediaFrom(path: File): Media {
        return when (path.extension) {
            "jpg" -> Media.builder()
                .setPath(path.toUri())
                .setMimeType("image/jpeg")
                .setPreview(loadImage(path.absolutePath))
                .build()
            "png" -> Media.builder()
                .setPath(path.toUri())
                .setMimeType("image/jpeg")
                .setPreview(loadImage(path.absolutePath))
                .build()
            "mp4" -> Media.builder()
                .setPath(path.toUri())
                .setMimeType("video/mp4")
                .setPreview(loadFrame(path.absolutePath, 1000))
                .build()

            else -> throw IllegalArgumentException("file: $path is not a media file")
        }
    }
}