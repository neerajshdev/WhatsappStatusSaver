package com.nibodev.statussaver

import android.content.Context
import android.util.Log
import java.io.File


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

val debug = BuildConfig.DEBUG
inline fun isDebug(content: () -> Unit) {
    if (debug) {
        content()
    }
}


