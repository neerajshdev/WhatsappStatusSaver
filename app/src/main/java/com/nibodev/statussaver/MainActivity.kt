package com.nibodev.statussaver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import com.nibodev.statussaver.ui.MainUserInterface
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import java.io.File

const val TAG = "debugOut"
var pathToWhatsFiles = ""
var saved_media_dir: String? = null

class MainActivity : ComponentActivity() {

    lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    val permissions by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    lateinit var onStoragePermissionResult: (Boolean) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init directories
        val parentDir = getAbsoluteDir(this, null).absolutePath

        var pathToWhatsAppStatus = "$parentDir/WhatsApp/Media/.Statuses"
        if (File(pathToWhatsAppStatus).exists())
            pathToWhatsFiles = pathToWhatsAppStatus

        pathToWhatsAppStatus = "$parentDir/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        if (File(pathToWhatsAppStatus).exists())
          pathToWhatsFiles = pathToWhatsAppStatus

        saved_media_dir = if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R) "${getExternalFilesDir(null)!!.absolutePath}/StatusSaver" else "$parentDir/DCIM/StatusSaver"
        // make saved directory if it does not exists
        if (!File(saved_media_dir!!).exists()) File(saved_media_dir!!).mkdir()

        isDebug {
            Log.d(
                TAG,
                "whatsapp media dir = $pathToWhatsFiles"
            )
            Log.d(TAG, "saved media dir = $saved_media_dir")
        }

        loadInterstitialAd(this) { it.show(this) }

        requestPermissionLauncher = registerForActivityResult(RequestMultiplePermissions()) { result ->
            var accepted = true
            for (permission in permissions) {
                accepted = accepted.and(result[permission] ?: false)
            }
            onStoragePermissionResult.invoke(accepted)
        }

        initContent()
    }

    private fun isStoragePermissionsGranted(): Boolean {
        var grantPermissions = true
        for (permission in permissions) {
            val isGranted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            grantPermissions = grantPermissions.and(isGranted)
            isDebug {
                Log.d(TAG, "$permission: $isGranted")
            }
        }
        return grantPermissions
    }

    private fun initContent() {
        val model: MainViewModel by viewModels()
        model.init(this)

        setContent {
            val isStoragePermissionsGranted = remember {
                mutableStateOf(isStoragePermissionsGranted())
            }
            if (!isStoragePermissionsGranted.value) {
                onStoragePermissionResult = { result ->
                    isStoragePermissionsGranted.value = result
                    if (!result) {
                        requestPermissionLauncher.launch(permissions)
                        Toast.makeText(this, "Please, allow for storage permissions.", Toast.LENGTH_LONG).show()
                    }
                }
                requestPermissionLauncher.launch(permissions)
            } else {
                WhatsappStatusSaverTheme {
                    MainUserInterface(model = model)
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        loadInterstitialAd(this) { it.show(this) }
    }
}
