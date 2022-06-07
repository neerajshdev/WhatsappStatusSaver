package com.nibodev.statussaver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import java.io.InputStream
import java.io.OutputStream


fun formatTime(timeInMillis: Long): String {
    var seconds = (timeInMillis / 1000).toInt()
    val minutes = seconds / 60
    seconds %= 60
    var time = ""

    if (minutes < 10) time += "0$minutes" else time += minutes
    time += ":"
    if (seconds < 10) time += "0$seconds" else time += seconds
    return time
}

/**
fun getAbsoluteDir(ctx: Context, optionalPath: String?): File {
    var rootPath: String = if (optionalPath != null && optionalPath != "") {
        ctx.getExternalFilesDir(optionalPath)!!.absolutePath
    } else {
        ctx.getExternalFilesDir(null)!!.absolutePath
    }

    isDebug { Log.d(TAG, "rootPath = $rootPath") }
    // extraPortion is extra part of file path
    val extraPortion = ("Android/data/" + BuildConfig.APPLICATION_ID
            + File.separator + "files" )
    isDebug { Log.d(TAG, "extraPortion = $extraPortion") }

    // Remove extraPortion

    rootPath = rootPath.replace(extraPortion, "")
    isDebug { Log.d(TAG, "after removing extra portion rootPath = $rootPath") }

    return File(rootPath)
}
*/

val debug = BuildConfig.DEBUG
inline fun isDebug(content: () -> Unit) {
    if (debug) {
        content()
    }
}

// print msg with console tag if the app is in debug mode
fun console(msg: String)  {
    if (debug)
        Log.d("Console", msg)
}

/**
 * Queries for network availability
 * @return true if network is available otherwise false
 */
fun isNetworkConnected(context: Context): Boolean {
    var connected = false
    if (context is Activity) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null) connected = netInfo.isAvailable && netInfo.isConnectedOrConnecting
    }
    return connected
}


/**
 * Checks whether the com.whatsapp package is installed on this device
 */
fun isWhatsappInstalled(packageName: String, pm: PackageManager): Boolean {
    val isInstalled: Boolean = try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return isInstalled
}


fun shareThisApp(activity: Activity) {
    val myIntent = Intent(Intent.ACTION_SEND)
    myIntent.type = "text/plain"
    val body = Firebase.remoteConfig.getString("share_text_body")
    val sub = Firebase.remoteConfig.getString("share_sub_body")
    myIntent.putExtra(Intent.EXTRA_SUBJECT, sub)
    myIntent.putExtra(Intent.EXTRA_TEXT, body)
    activity.startActivity(Intent.createChooser(myIntent, "Share Using"))
}

fun openPrivacyPolicyInWeb(activity: Activity) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://w3cleverprogrammer.blogspot.com/p/wa-saver-privacy-policy.html")
    }
    activity.startActivity(Intent.createChooser(intent, "View in"), )
}



/**
 * Creates an intent to open an uri tree
 * @param initialPath : the path which should be open in document provider ui
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun buildOpenUriTreeIntent(context: Context, initialPath: String) : Intent{
    val sm = context.getSystemService(ComponentActivity.STORAGE_SERVICE) as StorageManager
    val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
    var uri = intent.getParcelableExtra<Uri>(DocumentsContract.EXTRA_INITIAL_URI)
    var scheme = uri.toString()
    scheme = scheme.replace("/root/", "/document/")
    scheme += "%3A" + initialPath.replace("/", "%2f")
    uri = Uri.parse(scheme)
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
    return intent
}


fun copy(from: InputStream, to: OutputStream) {
    val buffer = ByteArray(1024)
    var len = from.read(buffer)
    while (len > 0) {
        to.write(buffer, 0, len)
        len = from.read(buffer)
    }
}