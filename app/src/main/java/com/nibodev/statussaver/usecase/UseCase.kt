package com.nibodev.statussaver.usecase

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.nibodev.statussaver.console
import com.nibodev.statussaver.copy
import com.nibodev.statussaver.models.ImageMedia
import com.nibodev.statussaver.models.Media
import com.nibodev.statussaver.models.VideoMedia
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var returnedSample = 1
    var inSample = 2
    while (height / inSample >= reqHeight || width / inSample >= reqWidth) {
        returnedSample = inSample
        inSample *= 2
    }
    console("returned inSampleSize = $returnedSample for actual size = $width x $height and requested size = $reqWidth x $reqHeight")
    return returnedSample
}

/**
 * Load image from a content uri.
 * Loaded image will be nearly scaled down to requested size
 */
class LoadImageUseCase(
    private val filepath: String,
    private val reqWidth: Int,
    private val reqHeight: Int
) {
    operator fun invoke() = try {
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(filepath, this)
            inJustDecodeBounds = false
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            BitmapFactory.decodeFile(filepath, this).also {
                console("loaded image from = $filepath, scaled size = ${it.width} x ${it.height}")
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}


/**
 * Load image from a content uri.
 * Loaded image will be nearly scaled down to requested size
 *
 */
class LoadContentImageUseCase(
    val contentResolver: ContentResolver,
    val content: Uri,
    val reqWidth: Int,
    val reqHeight: Int
) {
    operator fun invoke() = try {
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            contentResolver.openFileDescriptor(content, "r")
                .use { BitmapFactory.decodeFileDescriptor(it!!.fileDescriptor) }

            inJustDecodeBounds = false
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            contentResolver.openFileDescriptor(content, "r")
                .use { BitmapFactory.decodeFileDescriptor(it!!.fileDescriptor) }.also {
                    console("loaded image from = $content, scaled size = ${it.width} x ${it.height}")
                }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}


/**
 * Use case for loading the whatsapp media content from local storage
 * using the file api, calling invoke returns a list of ImageMedia.
 *
 * before using it make sure you have the read storage permission (only for android api level <= 28)
 *
 * @param pathToWhatsApp: path to whatsapp media/.Statuses folder
 */
class LoadWhatsAppMediaUseCase(
    val pathToWhatsApp: String,
) {
    operator fun invoke(): List<Media> {
        val mediaList = mutableListOf<Media>()
        try {
            val children = File(pathToWhatsApp).listFiles()
            if (children != null) {
                for(child in children) {
                    if (child.isFile && child.canRead()) {
                        try {
                            mediaList.add(createMediaObject(child))
                        } catch (ex: IllegalArgumentException) {
                            ex.printStackTrace()
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return mediaList
    }

    /**
     * converts the media represented by this file to Media object
     */
    private fun createMediaObject(mediaFile: File) : Media {
        return when (mediaFile.extension) {
            "png", "jpg", "jpeg"-> {
                ImageMedia(mediaFile.absolutePath)
            }
            // TODO: add support to video media
            "mp4" -> {VideoMedia(mediaFile.absolutePath)}
            else -> throw IllegalArgumentException("$mediaFile is not a media so couldn't convert it to Media object")
        }
    }
}


/**
 * Use case for loading the whatsapp media content from local storage
 * using the document file api, calling invoke returns a list of ImageMedia.
 *
 * before using it make sure you have the read storage permission (only for android api level <= 28)
 *
 * @param documentFile: DocumentFile to whatsapp media/.Statuses folder
 */
class LoadWhatsappMediaContentUseCase(
    val documentFile: DocumentFile,
) {
    operator fun invoke(): List<Media> {
        val mediaList = mutableListOf<Media>()
        try {
            val children = documentFile.listFiles()
            for(child in children) {
                if (child.isFile && child.canRead()) {
                    try {
                        mediaList.add(createMediaObject(child))
                    } catch (ex: IllegalArgumentException) {
                        ex.printStackTrace()
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return mediaList
    }

    /**
     * converts the media represented by this file to Media object
     */
    private fun createMediaObject(mediaFile: DocumentFile) : Media {
        return when (mediaFile.type) {
            "image/png", "image/jpeg"-> {
                ImageMedia(mediaFile.uri.toString())
            }
            // TODO: add support to video media
//            "video/mp4" -> {}
            else -> throw IllegalArgumentException("$mediaFile is not a media so couldn't convert it to Media object")
        }
    }
}


/**
 * Save file to another location
 * @param file path to a file
 * @param saveTo path to a directory
 */
suspend fun downloadStatusUseCase(
    file: String, saveTo: String, onComplete: suspend (String) -> Unit
) {
    val inputFile = File(file)
    val name = "$saveTo/${getNewName()}.${inputFile.extension}"
    val input = inputFile.inputStream()
    val output = File(name).outputStream()
    copy(input, output)
    input.close()
    output.close()
    onComplete(name)
}

suspend fun downloadStatusUseCase(uri: Uri, contentResolver: ContentResolver, saveTo: String, onComplete: suspend (String) -> Unit) {
    val ext = uri.toString().run { substring(lastIndexOf(".") + 1) }
    val name = "$saveTo/${getNewName()}.${ext}"
    val input = contentResolver.openInputStream(uri)
    val output = File(name).outputStream()
    if (input != null) {
        copy(input, output)
        onComplete(name)
    }
    input?.close()
    output.close()
}

private fun getNewName() : String {
    val date = Date()
    val formatter = SimpleDateFormat("dd-MM-yyyy hh.mm.ss", Locale.getDefault())
    return formatter.format(date)
}