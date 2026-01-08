package com.fastjetservice.photoresizer.data

import android.content.Context
import android.net.Uri
import com.antonkarpenko.ffmpegkit.FFmpegKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FFmpegImageCompressor(private val context: Context) {

    suspend fun compressForPreview(
        inputUri: Uri,
        width: Int,
        height: Int,
        format: String
    ): File = withContext(Dispatchers.IO) {

        val inputFile = File(context.cacheDir, "input.$format")
        context.contentResolver.openInputStream(inputUri)?.use {
            it.copyTo(inputFile.outputStream())
        }

        val previewFile = File(context.cacheDir, "preview.$format")

        val command =
            "-y -i ${inputFile.absolutePath} -vf scale=$width:$height -q:v 20 ${previewFile.absolutePath}"

        val session = FFmpegKit.execute(command)
        if (!session.returnCode.isValueSuccess) {
            throw RuntimeException("Preview FFmpeg failed")
        }

        previewFile
    }

    suspend fun compressForFinalSave(
        inputUri: Uri,
        width: Int,
        height: Int,
        quality: Int,
        format: String,
        outputFile: File
    ) = withContext(Dispatchers.IO) {

        val inputFile = File(context.cacheDir, "input_final.$format")
        context.contentResolver.openInputStream(inputUri)?.use {
            it.copyTo(inputFile.outputStream())
        }

        val qv = (31 - (quality / 100.0 * 29)).toInt().coerceIn(2, 31)

        val command =
            "-y -i ${inputFile.absolutePath} -vf scale=$width:$height -q:v $qv ${outputFile.absolutePath}"

        val session = FFmpegKit.execute(command)
        if (!session.returnCode.isValueSuccess) {
            throw RuntimeException("Final FFmpeg failed")
        }
    }

}

/*suspend fun compress(
    inputUri: Uri,
    width: Int,
    height: Int,
    quality: Int,
    format: String
): File = withContext(Dispatchers.IO) {

    val inputFile = File(context.cacheDir, "input.${format.lowercase()}")
    context.contentResolver.openInputStream(inputUri)?.use {
        it.copyTo(inputFile.outputStream())
    }

    // 👈 خروجی موقت
    val outputFile = File(
        context.cacheDir,
        "preview.${format.lowercase()}"
    )

    val qv = (31 - (quality / 100.0 * 29))
        .toInt()
        .coerceIn(2, 31)

    val command = if (format.equals("png", true)) {
        "-y -i ${inputFile.absolutePath} -vf scale=$width:$height ${outputFile.absolutePath}"
    } else {
        "-y -i ${inputFile.absolutePath} -vf scale=$width:$height -q:v $qv ${outputFile.absolutePath}"
    }

    val session = FFmpegKit.execute(command)
    if (!session.returnCode.isValueSuccess) {
        throw RuntimeException("FFmpeg failed")
    }

    outputFile // 👈 فقط preview
}
}*/
/*class FFmpegImageCompressor(private val context: Context) {

    suspend fun compress(
        inputUri: Uri,
        width: Int,
        height: Int,
        quality: Int,
        format: String
    ): File = withContext(Dispatchers.IO) {

        val inputStream = context.contentResolver.openInputStream(inputUri)
        val inputFile = File(context.cacheDir, "input.${format.lowercase()}")
        inputStream?.use { it.copyTo(inputFile.outputStream()) }

        val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.cacheDir

        val originalName = File(inputUri.path ?: "image").nameWithoutExtension
        val outputFile =
            File(picturesDir, "${originalName}_${width}x${height}.${format.lowercase()}")

        val qv = (31 - (quality / 100.0 * 29)).toInt().coerceIn(2, 31)
        val command = if (format.equals("png", ignoreCase = true)) {
            "-y -i ${inputFile.absolutePath} -vf scale=$width:$height ${outputFile.absolutePath}"
        } else {
            "-y -i ${inputFile.absolutePath} -vf scale=$width:$height -q:v $qv ${outputFile.absolutePath}"
        }

        val session = FFmpegKit.execute(command)
        if (!session.returnCode.isValueSuccess) {
            throw RuntimeException("FFmpeg failed: ${session.failStackTrace}")
        }

        outputFile
    }
}*/
