package com.softneez.wasaver

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.getSystemService
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softneez.wasaver.models.Media
import com.softneez.wasaver.models.StatusImage
import com.softneez.wasaver.models.StatusVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.ref.WeakReference

const val videoExtension = ".mp4"
const val imageExtension = ".jpg"

class MainViewModel : ViewModel() {
    lateinit var selectMediaResultLauncher: ActivityResultLauncher<Intent>
    private val desiredUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia")
    private val repository: Repository by lazy { Repository() }
    private lateinit var context: WeakReference<Context>

    private lateinit var mediaFiles: List<String>
    private lateinit var savedMediaFiles: List<String>

    private val savedMediaMap = mutableMapOf<String, Boolean>()

    // ui data
    val statusVideo = mutableStateListOf<StatusVideo>()
    val statusImage = mutableStateListOf<StatusImage>()
    val savedImage = mutableStateListOf<StatusImage>()
    val savedVideo = mutableStateListOf<StatusVideo>()
    val recentMedia = mutableStateListOf<Media>()
    val savedMedia = mutableStateListOf<Media>()

    // gets true when user selects whats media on android 11 and this media is referenced via mediaUri
    val mediaSelected = mutableStateOf(true)
    private var mediaUri: Uri? = null

    // input data for video and image screen
    var videoEntry: StatusVideo? = null
    var imageEntry: StatusImage? = null


    private fun isUriHasPermission(uri: Uri): Boolean {
        val uriPermissions = context.get()!!.contentResolver.persistedUriPermissions
        for (uriPermission in uriPermissions) {
            if (uriPermission.uri == uri) {
                return true
            }
        }
        return false
    }

    private fun persistGrantsUri(uri: Uri) {
        val contentResolver = context.get()!!.contentResolver
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // called when the activity is created
    fun init(context: Context) {
        this.context = WeakReference(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activity = context as MainActivity
            if (!isUriHasPermission(desiredUri)) {
                mediaSelected.value = false
            } else {
                mediaUri = desiredUri
            }
            // user needs to select the whatsapp media
            selectMediaResultLauncher =
                activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val selectedUri = result.data!!.data
                        if (selectedUri == desiredUri) {
                            mediaUri = selectedUri
                            mediaSelected.value = true
                            persistGrantsUri(selectedUri!!)
                        }
                        isDebug {
                            Log.d(TAG, "Media selected = ${result.data!!.data}")
                        }
                    }
                }
        }
    }


    fun initMedia() {
        viewModelScope.launch(Dispatchers.Default) {
            updateMediaFiles()
            updateMap()
            updateMedia()
            // Saved Media
            updateSavedMedia()

            // inner function
            fun printFiles(files: List<String>): String {
                var out = ""
                for (file in files) {
                    out += file + "\n"
                }
                return out
            }

            isDebug {
                Log.d(
                    TAG, "MainViewModel Data initialized =>\n" +
                            "WhatsApp mediaFiles = ${
                                printFiles(mediaFiles)
                            }" + "Saved media files = ${printFiles(savedMediaFiles)}"
                )
            }
        }
    }


    private fun updateMediaFiles() {
        mediaFiles = if (isSdk11Up()) {
            repository.getDocument(context.get()!!, findStatus(mediaUri!!))
        } else {
            repository.getListFiles(File(pathToWhatsFiles))
        }
        savedMediaFiles = repository.getListFiles(File(saved_media_dir!!))
    }


    private fun findStatus(uri: Uri) : Uri{
        val documentTree = DocumentFile.fromTreeUri(context.get()!!, uri)
        return  documentTree!!.findFile(".Statuses")!!.uri
    }


    private fun isSdk11Up(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    // helper function for user to select the media folder
    @RequiresApi(Build.VERSION_CODES.R)
    fun selectMedia() {
       requestInitialUri()
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


    fun saveFile(src: String, onSuccess: (String) -> Unit) {
        val name = File(src).name
        val srcContent = if (src.startsWith("content")) {
            context.get()!!.contentResolver.openInputStream(Uri.parse(src))
        } else {
            FileInputStream(src)
        }
        val dest = FileOutputStream("$saved_media_dir/$name")

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveContent(srcContent!!, dest)
            }
            onSuccess.invoke("$saved_media_dir/$name")
            withContext(Dispatchers.Default) {
                savedMediaFiles = repository.getListFiles(File(saved_media_dir!!))
                updateSavedMedia()
            }
        }
    }


    private fun videoFiles(): List<String> {
        return mediaFiles.filter { it.endsWith(videoExtension) }
    }

    private fun imageFiles(): List<String> {
        return mediaFiles.filter { it.endsWith(imageExtension) }
    }


    private fun updateSavedMedia() {
        savedVideo.clear()
        savedImage.clear()
        savedMedia.clear()
        val savedVideoFiles = savedMediaFiles.filter { it.endsWith(videoExtension) }
        repository.loadStatusVideo(context.get()!!, savedVideoFiles, savedVideo, savedMediaMap)

        val savedImageFiles = savedMediaFiles.filter { it.endsWith(imageExtension) }
        repository.loadStatusImage(context.get()!!, savedImageFiles, savedImage, savedMediaMap)

        savedMedia.addAll(savedImage)
        savedMedia.addAll(savedVideo)
        savedMedia.shuffle()
    }


    private fun updateMedia() {
        statusVideo.clear()
        statusImage.clear()
        recentMedia.clear()
        val videoFiles = videoFiles()
        repository.loadStatusVideo(context.get()!!, videoFiles, statusVideo, savedMediaMap)

        val imgFiles = imageFiles()
        repository.loadStatusImage(context.get()!!, imgFiles, statusImage, savedMediaMap)
        recentMedia.addAll(statusVideo)
        recentMedia.addAll(statusImage)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestInitialUri() {
        val sm = context.get()!!.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        var uri = intent.getParcelableExtra<Uri>(DocumentsContract.EXTRA_INITIAL_URI)
        val startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia"
        var scheme = uri.toString()

        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"

        isDebug {
            Log.d(TAG, "scheme: $scheme")
        }

        uri = Uri.parse(scheme)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        selectMediaResultLauncher.launch(intent)
    }
}