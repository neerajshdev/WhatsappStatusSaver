package com.nibodev.composeapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CompletableDeferred
import java.lang.ref.WeakReference

object PermissionManager {
    val initialUri =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia")
    private var context: WeakReference<Context>? = null

    private lateinit var openDocumentTree: ActivityResultLauncher<Uri?>
    private lateinit var reqPermission: ActivityResultLauncher<Array<String>>
    private val deferred: CompletableDeferred<Boolean> = CompletableDeferred()

    /**
     * Should be called in the Activity's on create method
     */
    fun initWith(context: MainActivity): PermissionManager {
        this.context = WeakReference(context)
        openDocumentTree =
            context.registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                if (uri != null) {
                    if (uri == Constants.whatsappDocumentUri) {
                        deferred.complete(true)
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

                } else {
                    deferred.complete(false)
                }
            }
        reqPermission =
            context.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                val grant = result[Manifest.permission.READ_EXTERNAL_STORAGE]!! &&
                        result[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!
                deferred.complete(grant)
            }
        return this
    }

    /**
     * if one of the storage permissions (write and read) is denied then this function returns true
     */
    fun ifNeedToAskStoragePermission(): Boolean {
        val ctx = requireNotNull(context?.get())
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        }
        return false
    }

    fun ifNeedToAskDocumentUri(): Boolean {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            val uris = requireNotNull(context?.get()).contentResolver.persistedUriPermissions
            for (uri in uris) {
                if (uri.uri == Constants.whatsappDocumentUri) return false
            }
        }
        return false
    }

    suspend fun askForStoragePermission() : Boolean {
        val perms: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        reqPermission.launch(perms)
        return deferred.await()
    }

    suspend fun askForDocumentUri(): Boolean {
        openDocumentTree.launch(initialUri)
        return deferred.await()
    }
}