package com.nibodev.composeapp

import android.net.Uri
import android.os.Environment

object Constants {
    val whatsappDocumentUri: Uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia")
    val whatsappPath = "${Environment.getExternalStorageDirectory()}/WhatsApp/Media/.Statuses"
    val downloadPath = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DCIM}/GbSaver"
}