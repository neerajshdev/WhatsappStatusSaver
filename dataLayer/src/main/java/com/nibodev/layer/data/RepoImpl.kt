package com.nibodev.layer.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import copyTo
import getDocumentFilesInFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import loadFrame
import loadImage
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repo implementation for android sdk 29 and above
 */

class RepoImpl : Repo {
    var context : WeakReference<Context>? = null
    private lateinit var baseUri: Uri
    private lateinit var downloadLoc: String

    /**@return : Flow from which caller can collect statuses
     * @exception: throws Exceptions.RequiredPersistedMediaUri when required media uri (path to whatsapp media) not found in contentResolver
     */
    override fun loadMedia(): Flow<Media> {
        val parent = DocumentFile.fromTreeUri(requireNotNull(context?.get()), baseUri)
        // todo: please improve the exception handling here (parent might be null)
        return getDocumentFilesInFlow(parent!!)
            .filter {
                isMediaFile(it)
            }
            .map {
                mediaFrom(it)
            }
    }

    fun with(context: Context) : RepoImpl {
        this.context = WeakReference(context)
        return this
    }

    fun baseUri(uri: Uri) : RepoImpl {
        this.baseUri = uri
        return this
    }

    fun downloadLoc(folder: String): RepoImpl {
        downloadLoc = folder
        return this
    }

    override fun download(media: Media) {
        val uri = media.uri
        val dest = downloadLoc
        download(uri,dest)
    }

    // for android versions > 9
    // does not require write external permission
    private fun download(uri: Uri, dest: String) {
        val name : String = createName()
        val oStream = FileOutputStream(dest + name)
        val iStream = requireNotNull(context?.get()).contentResolver.openInputStream(uri)!!
        copyTo(iStream, oStream)
    }

    private fun createName() : String {
        val time = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())
        return formatter.format(time)
    }


    private fun isMediaFile(it: DocumentFile) : Boolean{
        return when(it.type) {
            "video/mp4" -> true
            "image/jpeg" -> true
            else -> false
        }
    }


    private fun mediaFrom(dFile: DocumentFile): Media {
        val ctx = requireNotNull(context?.get())
        return when (dFile.type) {
            "image/jpeg" -> Media.builder()
                .setPath(dFile.uri)
                .setMimeType("image/jpeg")
                .setPreview(loadImage(ctx, dFile.uri))
                .build()
            "image/png" -> Media.builder()
                .setPath(dFile.uri)
                .setMimeType("image/jpeg")
                .setPreview(loadImage(ctx, dFile.uri))
                .build()
            "video/mp4" -> Media.builder()
                .setPath(dFile.uri)
                .setMimeType("image/jpeg")
                .setPreview(loadFrame(ctx, dFile.uri, 1000))
                .build()

            else -> throw IllegalArgumentException("file: ${dFile.uri} is not a media file")
        }
    }
}