package com.softneez.wasaver

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.softneez.wasaver.models.StatusImage
import com.softneez.wasaver.models.StatusVideo
import java.io.File

class Repository {
    fun getListFiles(parentDir: File): List<String> {
        val paths = mutableListOf<String>()
        val files = parentDir.listFiles()

        if (files != null) {
            for (file in files) {
                Log.e("check", file.name)
                if (file.name.endsWith(".jpg") ||
                    file.name.endsWith(".gif") ||
                    file.name.endsWith(".mp4")
                ) {
                    if (!paths.contains(file.absolutePath)) paths.add(file.absolutePath)
                }
            }
        }
        return paths
    }



    private fun loadImage(from: String): ImageBitmap {
        val bitmap = BitmapFactory.decodeFile(from)
        return bitmap.asImageBitmap()
    }

    private fun getVideoFrame(src: String, time: Long ): ImageBitmap {
        val vmr = MediaMetadataRetriever()
        // extract data
        vmr.setDataSource(src)
        val bitmap = vmr.getFrameAtTime(time)
        vmr.release()
        return bitmap!!.asImageBitmap()
    }


//    fun getVideoFrames(src: List<String>, time: Long): MutableList<ImageBitmap> {
//        val vmr = MediaMetadataRetriever()
//
//        val imageBitmaps = mutableListOf<ImageBitmap>()
//        src.forEach {
//            vmr.setDataSource(it)
//            val image = vmr.getFrameAtTime(time)?.asImageBitmap()
//            imageBitmaps.add(image!!)
//        }
//        vmr.release()
//
//        return imageBitmaps
//    }

    private fun getVideoDuration(src: String): String {
        val vmr = MediaMetadataRetriever()
        vmr.setDataSource(src)
        val duration = vmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val len = formatTime(duration!!.toLong())
        vmr.release()
        return len
    }

    // fill data into mutableStateList
    fun loadStatusVideo(
        paths: List<String>,
        into: SnapshotStateList<StatusVideo>,
        saved: Map<String, Boolean>
    ) {
        for (path in paths) {
            // get frame at 1000 ms
            val imageBitmap = getVideoFrame(path, 1000)
            val videoLen = getVideoDuration(path)
            val video = StatusVideo(path, imageBitmap, videoLen, saved[File(path).name]!!)
            into.add(video)
        }
    }

    fun loadStatusImage(
        paths: List<String>,
        into: SnapshotStateList<StatusImage>,
        saved: Map<String, Boolean>
    ) {
        for (path in paths) {
            val imageBitmap = loadImage(path)
            val image = StatusImage(path, imageBitmap, mutableStateOf(saved[File(path).name]!!))
            into.add(image)
        }
    }

    fun saveFile(from: File, to: File) {
        val fis = from.inputStream()
        val fos = to.outputStream()
        val data = fis.readBytes()
        fos.write(data)
        fis.close()
        fos.close()
    }
}