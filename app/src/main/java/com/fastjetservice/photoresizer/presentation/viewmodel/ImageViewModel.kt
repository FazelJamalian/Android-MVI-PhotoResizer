package com.fastjetservice.photoresizer.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastjetservice.photoresizer.data.FFmpegImageCompressor
import com.fastjetservice.photoresizer.domain.model.ImageState
import com.fastjetservice.photoresizer.presentation.intent.ImageIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val ffmpegCompressor: FFmpegImageCompressor,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(ImageState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: ImageIntent) {
        when (intent) {
            is ImageIntent.PickImage -> {
                _state.value = _state.value.copy(imageUri = intent.uri)
                loadOriginalDimensions(intent.uri)
            }

            is ImageIntent.SetWidth -> {
                val maxWidth = _state.value.originalWidth ?: intent.width
                _state.value = _state.value.copy(width = intent.width.coerceAtMost(maxWidth))
                updateEstimatedSize()
            }

            is ImageIntent.SetHeight -> {
                val maxHeight = _state.value.originalHeight ?: intent.height
                _state.value = _state.value.copy(height = intent.height.coerceAtMost(maxHeight))
                updateEstimatedSize()
            }

            is ImageIntent.SetSize -> {
                updateEstimatedSize()
            }

            is ImageIntent.SetQuality -> {
                _state.value = _state.value.copy(quality = intent.quality)
                updateEstimatedSize()
            }

            is ImageIntent.SetFormat -> {
                _state.value = _state.value.copy(format = intent.format)
                updateEstimatedSize()
            }

            is ImageIntent.Compress -> compressImage(intent.uri)
            ImageIntent.Reset -> _state.value = ImageState()

            is ImageIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun getFileSize(uri: Uri): Long {
        val cursor: Cursor? = context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )
        return cursor?.use { c ->
            val sizeIndex = c.getColumnIndex(OpenableColumns.SIZE)
            c.moveToFirst()
            c.getLong(sizeIndex)
        } ?: 0
    }

    private fun loadOriginalDimensions(uri: Uri) {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
        val originalSize = getFileSize(uri)
        _state.value = _state.value.copy(
            originalWidth = options.outWidth,
            originalHeight = options.outHeight,
            originalSize = originalSize,
            width = options.outWidth,
            height = options.outHeight
        )
        updateEstimatedSize()
    }

    private fun updateEstimatedSize() {
        val s = _state.value
        s.imageUri?.let { uri ->
            val estimated = estimateFileSize(uri, s.width, s.height, s.quality, s.format)
            _state.value = s.copy(estimatedSize = estimated)
        }
    }

    private fun estimateFileSize(
        uri: Uri,
        width: Int,
        height: Int,
        quality: Int,
        format: String
    ): Long {
        val originalSize = state.value.originalSize ?: getFileSize(uri)
        if (originalSize == 0L) return 0L

        val originalWidth = state.value.originalWidth ?: return 0L
        val originalHeight = state.value.originalHeight ?: return 0L

        if (originalWidth == 0 || originalHeight == 0) return 0L

        val resolutionRatio = (width.toFloat() * height) / (originalWidth * originalHeight)

        val estimatedSize = when (format.lowercase()) {
            "png" -> {
                (originalSize * resolutionRatio).toLong()
            }
            "jpg", "jpeg" -> {
                val qualityFactor = quality / 100.0f
                (originalSize * resolutionRatio * qualityFactor ).toLong()
            }
            "webp" -> {
                val qualityFactor = quality / 100.0f
                (originalSize * resolutionRatio * qualityFactor * 0.7f).toLong()
            }
            else -> {
                (originalSize * resolutionRatio).toLong()
            }
        }

        return estimatedSize
    }


    private fun getUniqueFileName(baseName: String, format: String): String {
        val resolver = context.contentResolver
        var fileName = "$baseName.$format"
        var index = 1

        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"

        while (true) {
            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                arrayOf(fileName),
                null
            )
            cursor?.use {
                if (it.count == 0) break
            } ?: break

            fileName = "${baseName}_$index.$format"
            index++
        }
        return fileName
    }

    private fun saveToPictures(tempFile: File, fileName: String, format: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/$format")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw RuntimeException("Unable to create URI for image")

        resolver.openOutputStream(uri)?.use { output ->
            tempFile.inputStream().copyTo(output)
        }
        return uri
    }

    private fun compressImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val s = _state.value
                val tempFile = ffmpegCompressor.compress(
                    inputUri = uri,
                    width = s.width,
                    height = s.height,
                    quality = s.quality,
                    format = s.format
                )

                val originalName = File(uri.path ?: "image").nameWithoutExtension
                val baseName = "${originalName}_${s.width}x${s.height}"
                val uniqueFileName = getUniqueFileName(baseName, s.format)

                val finalUri = saveToPictures(tempFile, uniqueFileName, s.format)

                _state.value = s.copy(outputFileUri = finalUri)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
