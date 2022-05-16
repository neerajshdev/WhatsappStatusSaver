import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun getFilesInFlow(parent: File): Flow<File> = flow {
    visitFiles(parent) {
        emit(it)
    }
}

suspend fun visitFiles(parent: File, visitor: suspend (File) -> Unit) {
    val children = parent.listFiles()
    if (children != null)
        for (child in children) {
            if (child.isFile) {
                visitor.invoke(child)
            } else {
                visitFiles(child, visitor)
            }
        }
}


fun getDocumentFilesInFlow(parent: DocumentFile): Flow<DocumentFile> = flow {
    visitDocumentFiles(parent) {
        emit(it)
    }
}

suspend fun visitDocumentFiles(parent: DocumentFile, visitor: suspend (DocumentFile) -> Unit) {
    val children = parent.listFiles()
    for (child in children) {
        if (child.isFile) {
            visitor.invoke(child)
        } else {
            visitDocumentFiles(child, visitor)
        }
    }
}


fun copyTo(from: InputStream, to: OutputStream) {
    val buffer: ByteArray = ByteArray(1024)
    var len = from.read(buffer)
    while (len > 0) {
        to.write(buffer, 0, len)
        len = from.read(buffer)
    }
}


fun Console(msg: String) {
    Log.d("Console", msg)
}

fun loadImage(context: Context, uri: Uri): Bitmap {
    Console( "Loading image from uri: $uri")
    return context.contentResolver.openFileDescriptor(uri, "r")
        .use {
            BitmapFactory.decodeFileDescriptor(it!!.fileDescriptor)
        }
}


fun loadImage(filePath: String): Bitmap {
    return BitmapFactory.decodeFile(filePath)
}

fun loadFrame(filePath: String, atTimeMs: Long): Bitmap {
    val vmr = MediaMetadataRetriever()
    vmr.setDataSource(filePath)
    return vmr.getFrameAtTime(atTimeMs * 1000)!!
}

fun loadFrame(context: Context, uri: Uri, atTime: Long): Bitmap {
    val vmr = MediaMetadataRetriever()
    return context.contentResolver.openFileDescriptor(uri, "r").use {
        vmr.setDataSource(it!!.fileDescriptor)
        vmr.getFrameAtTime(atTime * 1000)!!
    }
}
