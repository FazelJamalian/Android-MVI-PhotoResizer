package com.fastjetservice.photoresizer.presentation.viewmodel

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastjetservice.photoresizer.data.FFmpegImageCompressor
import com.fastjetservice.photoresizer.domain.model.EditState
import com.fastjetservice.photoresizer.presentation.ui.intent.EditIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val ffmpegCompressor: FFmpegImageCompressor,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(EditState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: EditIntent) {
        when (intent) {
            is EditIntent.PickImage -> {
                _state.value = _state.value.copy(imageUri = intent.uri)
                loadOriginalDimensions(intent.uri)
            }

            is EditIntent.CompressedImage -> {
                _state.value = _state.value.copy(compressedUri = intent.uri)
                loadCompressedDimensions(intent.uri)
            }

            is EditIntent.Compress -> compressImage(intent.uri)
            EditIntent.Reset -> _state.value = EditState()

            is EditIntent.SaveToPhoto -> {
                saveFinalImageToGallery()
            }

            is EditIntent.SetWidth -> {
                val maxWidth = _state.value.originalWidth ?: intent.width
                _state.value = _state.value.copy(width = intent.width.coerceAtMost(maxWidth))

            }

            is EditIntent.SetHeight -> {
                val maxHeight = _state.value.originalHeight ?: intent.height
                _state.value = _state.value.copy(height = intent.height.coerceAtMost(maxHeight))

            }

            is EditIntent.SetSize -> {
                updateReducedSize()
            }

            is EditIntent.SetQuality -> {
                _state.value = _state.value.copy(quality = intent.quality)

            }

            is EditIntent.SetFormat -> {
                _state.value = _state.value.copy(format = intent.format)
                Log.d("TAG", "SetFormat: ${intent.format}")

            }


            is EditIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }
    private fun getFileSizeSmart(uri: Uri): Long {
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> getSizeFromContent(uri)
            ContentResolver.SCHEME_FILE -> File(uri.path!!).length()
            else -> 0L
        }
    }

    private fun getSizeFromContent(uri: Uri): Long {
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )

        return cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.SIZE)
            if (it.moveToFirst() && index != -1) it.getLong(index) else 0L
        } ?: 0L
    }

    private fun loadOriginalDimensions(uri: Uri = _state.value.compressedUri!!) {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
        val originalSize = getFileSizeSmart(uri)
        _state.value = _state.value.copy(
            originalWidth = options.outWidth,
            originalHeight = options.outHeight,
            originalSize = originalSize,
            width = options.outWidth,
            height = options.outHeight
        )
        loadCompressedDimensions(uri = uri)
        updateReducedSize()
    }

    private fun loadCompressedDimensions(uri: Uri) {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
        val originalSize = getFileSizeSmart(uri)
        _state.value = _state.value.copy(
            compressedWidth = options.outWidth,
            compressedHeight = options.outHeight,
            compressedSize = originalSize,
            width = options.outWidth,
            height = options.outHeight
        )
        updateReducedSize()
    }

    private fun updateReducedSize() {
        val s = _state.value
        val estimated = s.compressedSize?.let { s.originalSize?.minus(it) }
        _state.value = s.copy(reducedSize = estimated)

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

    private fun compressImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val s = _state.value

                val previewFile = ffmpegCompressor.compressForPreview(
                    inputUri = uri,
                    width = s.width,
                    height = s.height,
                    format = s.format
                )

                _state.value = s.copy(
                    compressedUri = Uri.fromFile(previewFile)
                )

                loadCompressedDimensions(Uri.fromFile(previewFile))

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }


    private fun saveFinalImageToGallery() {
        viewModelScope.launch {
            try {
                val s = _state.value
                val resolver = context.contentResolver

                val originalName =
                    File(s.imageUri?.path ?: "image").nameWithoutExtension

                val baseName = "${originalName}_ReSized"
                val uniqueName = getUniqueFileName(baseName, s.format)

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/${s.format}")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val uri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw RuntimeException("MediaStore insert failed")

                resolver.openOutputStream(uri)?.use { output ->
                    val tempFinalFile = File(context.cacheDir, "final.${s.format}")

                    ffmpegCompressor.compressForFinalSave(
                        inputUri = s.imageUri!!,
                        width = s.width,
                        height = s.height,
                        quality = s.quality,
                        format = s.format,
                        outputFile = tempFinalFile
                    )

                    tempFinalFile.inputStream().copyTo(output)
                    tempFinalFile.delete()
                }

                _state.value = s.copy(compressedUri = uri)

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

}
