package com.ns.whatsappstatussaver

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
import androidx.core.app.ActivityCompat
import com.ns.whatsappstatussaver.ui.MainUserInterface
import com.ns.whatsappstatussaver.ui.theme.WhatsappStatusSaverTheme
import java.io.File

const val TAG = "AppOut"
var WhatsApp_media_dir: String? = null
var Saved_media_dir: String? = null

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init directories
        val parentDir = getAbsoluteDir(this, null).absolutePath
        val pathToWhatsappStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) "Android/media/com.whatsapp/WhatsApp/Media/.Statuses/" else "WhatsApp/Media/.Statuses/"

        WhatsApp_media_dir = parentDir + File.separator + pathToWhatsappStatus
        Saved_media_dir = parentDir + File.separator + "DCIM/StatusSaver/"

        if (!File(Saved_media_dir!!).exists()) File(Saved_media_dir!!).mkdir()

        val filePath = Saved_media_dir + "WhatsAppStatusSaverLogcat.txt"
        Runtime.getRuntime().exec(
            arrayOf(
                "logcat",
                "-f",
                filePath,
                "--pid",
                android.os.Process.myPid().toString(),
                "ActivityManager:I",
                "MyApp:D",
                "*:D"
            )
        )

        isDebug {
            Log.d(
                TAG,
                "whatsapp media dir = $WhatsApp_media_dir : Exists = ${File(WhatsApp_media_dir!!).exists()}"
            )
            Log.d(TAG, "saved media dir = $Saved_media_dir")
        }

        loadInterstitialAd(this) { it.show(this) }
        checkExternalStoragePermissions()
    }

    private fun requestPermissionLauncher(onResponse: (Map<String, Boolean>) -> Unit): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(RequestMultiplePermissions()) { grantResult ->
            onResponse.invoke(grantResult)
        }
    }

    private fun checkExternalStoragePermissions() {
        // check for write external storage permission
        val canWrite =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        val canRead =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        isDebug {
            Log.d(TAG, "canWrite: $canWrite")
            Log.d(TAG, "canRead: $canRead")
        }

        if (canRead && canWrite) {
            // Display the ui
            initContent()
        } else {
            //  permission result
            val requestPermission = requestPermissionLauncher { result ->
                val writeExternalStorage = result[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!
                val readExternalStorage = result[Manifest.permission.READ_EXTERNAL_STORAGE]!!
                isDebug {
                    Log.d(TAG, "Permission Result ->")
                    Log.d(TAG, "write_external_storage: $writeExternalStorage")
                    Log.d(TAG, "read_external_storage: $readExternalStorage")
                }

                val allAccepted = writeExternalStorage && readExternalStorage

                if (!allAccepted) {
                    Toast.makeText(this, "Required permissions were disallowed!", Toast.LENGTH_LONG)
                        .show()
                    finish()
                } else {
                    initContent()
                }
            }

            // Request the permissions
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    private fun initContent() {
        val model: MainViewModel by viewModels()
        model.init()
        setContent {
            WhatsappStatusSaverTheme {
                MainUserInterface(model = model)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        loadInterstitialAd(this) { it.show(this) }
    }
}
