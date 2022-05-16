package com.nibodev.layer.data

import android.graphics.Bitmap
import android.net.Uri

sealed class Media(
    val preview: Bitmap,
    val mimeType: String,
    val uri: Uri
) {
    class Video(preview: Bitmap, path: Uri, mimeType: String) : Media(preview, mimeType, path)
    class Image(preview: Bitmap, path: Uri, mimeType: String) : Media(preview, mimeType, path)

    companion object {
        fun builder() : Builder {
            return Builder()
        }
    }

    override fun toString(): String {
        return "(mimetype: $mimeType, path: $uri, preview: $preview)"
    }

    class Builder {
        private lateinit var _preview: Bitmap
        private lateinit var _mimeType: String
        private lateinit var uri: Uri

        fun setPreview(prev: Bitmap): Builder {
            _preview = prev
            return this
        }

        fun setMimeType(type: String): Builder {
            _mimeType = type
            return this
        }

        fun setPath(uri : Uri): Builder {
            this.uri = uri
            return this
        }

        fun build() : Media {
            return when(_mimeType) {
                "video/mp4" -> Video(_preview, uri, _mimeType)
                "image/jpeg" -> Image(_preview, uri, _mimeType)
                "image/png" -> Image(_preview, uri, _mimeType)
                else -> throw IllegalArgumentException("mime type: $_mimeType does not represent a media")
            }
        }
    }
}

