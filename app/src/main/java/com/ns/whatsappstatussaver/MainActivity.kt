package com.ns.whatsappstatussaver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.viewModels
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.ns.whatsappstatussaver.ui.MainUserInterface
import com.ns.whatsappstatussaver.ui.theme.WhatsappStatusSaverTheme

const val TAG = "Debug"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAds()
        loadInterstitialAd(this) {
            it.show(this)
        }
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
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val canRead =
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (canRead && canWrite) {
            // Display the ui
            initContent()
        } else {
            //  permission result
            val requestPermission = requestPermissionLauncher { result ->
                val accepted = result[Manifest.permission.WRITE_EXTERNAL_STORAGE]!! &&
                        result[Manifest.permission.READ_EXTERNAL_STORAGE]!!
                if (!accepted) {
                    Toast.makeText(this, "Required permissions were disallowed!", Toast.LENGTH_LONG)
                        .show()
                    finish()
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

    private fun initAds() {
        val appID = Firebase.remoteConfig.getString("app_id")
        if (appID.isBlank()) {
            isDebug { Log.d(TAG, "initAds(): updating ads config") }
            fetchAdConfig {
                initAds()
            }
            return
        }
        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val bundle = appInfo.metaData
        isDebug { Log.d(TAG, "initAds(): initializing the mobile Ads....") }
        // todo 'replace null with appID'

        bundle.putString("com.google.android.gms.ads.APPLICATION_ID", appID)
        isDebug { Log.d(TAG, "initAds(): app id value = ${bundle.getString("com.google.android.gms.ads.APPLICATION_ID")}") }
        MobileAds.initialize(this) {}
    }


    override fun onRestart() {
        super.onRestart()
        loadInterstitialAd(this) { it.show(this) }
    }

}
