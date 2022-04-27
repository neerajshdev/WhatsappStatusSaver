package com.nibodev.statussaver

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.documentfile.provider.DocumentFile
import com.nibodev.statussaver.models.StatusImage
import com.nibodev.statussaver.models.StatusVideo
import java.io.*

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

    // uri should be whatsapp .Statuses folder
    fun getDocument(context: Context, uri: Uri): List<String> {
        val document = DocumentFile.fromTreeUri(context, uri)
        val uris = mutableListOf<String>()
        if (document != null && document.exists()) {
            for (docFile in document.listFiles()) {
                if (docFile.uri.toString().endsWith(imageExtension) ||
                    docFile.uri.toString().endsWith(videoExtension)
                )
                    uris.add(docFile.uri.toString())
            }
        }
        return uris
    }

    private fun loadImage(context: Context, from: String): ImageBitmap {
        val bitmap = if (from.startsWith("content")) {
            context.contentResolver.openFileDescriptor(Uri.parse(from), "r").use {
                BitmapFactory.decodeFileDescriptor(it!!.fileDescriptor)
            }
        } else {
            BitmapFactory.decodeFile(from)
        }
        return bitmap.asImageBitmap()
    }

    private fun getVideoFrame(context: Context, src: String, time: Long): ImageBitmap {
        val vmr = MediaMetadataRetriever()
        // extract data
        if (src.startsWith("content")) {
            vmr.setDataSource(context, Uri.parse(src))
        } else {
            vmr.setDataSource(src)
        }
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

    private fun getVideoDuration(context: Context, src: String): String {
        val vmr = MediaMetadataRetriever()
        if (src.startsWith("content")) {
//            val fd = context.contentResolver.openFileDescriptor(Uri.parse(src), "r").use { it!!.fileDescriptor }
            vmr.setDataSource(context, Uri.parse(src))
        } else {
            vmr.setDataSource(src)
        }
        val duration = vmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val len = formatTime(duration!!.toLong())
        vmr.release()
        return len
    }

    // fill data into mutableStateList
    fun loadStatusVideo(
        context: Context,
        paths: List<String>,
        into: SnapshotStateList<StatusVideo>,
        saved: Map<String, Boolean>
    ) {
        for (path in paths) {
            // get frame at 1000 ms
            val imageBitmap = getVideoFrame(context, path, 1000)
            val videoLen = getVideoDuration(context, path)
            val video = StatusVideo(path, imageBitmap, videoLen, saved[File(path).name]!!)
            into.add(video)
        }
    }

    fun loadStatusImage(
        context: Context,
        paths: List<String>,
        into: SnapshotStateList<StatusImage>,
        saved: Map<String, Boolean>
    ) {
        for (path in paths) {
            val imageBitmap = loadImage(context, path)
            val image = StatusImage(path, imageBitmap, mutableStateOf(saved[File(path).name] ?: false))
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

    fun saveContent(from: InputStream, to: OutputStream) {
        val data = from.readBytes()
        to.write(data)

        from.close()
        to.close()
    }
}