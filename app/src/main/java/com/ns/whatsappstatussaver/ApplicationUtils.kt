package com.ns.whatsappstatussaver


fun formatTime(timeInMillis: Long) :String {
    var seconds = (timeInMillis / 1000).toInt()
    val minutes = seconds / 60
    seconds %= 60
    var time = ""

    if (minutes < 10) time += "0$minutes" else time += minutes
    time += ":"
    if (seconds < 10) time += "0$seconds" else time += seconds
    return time
}

val debug = BuildConfig.DEBUG
inline fun isDebug(content: () -> Unit) {
    if (debug) {
        content()
    }
}


