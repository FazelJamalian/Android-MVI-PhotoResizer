package com.fastjetservice.photoresizer.domain.model

import android.net.Uri

data class EditState(
    val imageUri: Uri? = null,
    val originalWidth: Int? = null,
    val originalHeight: Int? = null,
    val originalSize: Long? = null,
    val width: Int = 1000,
    val height: Int = 1000,
    val quality: Int = 80,
    val format: String = "jpg",
    val isResizing: Boolean = false,
    val compressedUri: Uri? = null,
    val compressedWidth: Int? = null,
    val compressedHeight: Int? = null,
    val compressedSize: Long? = null,
    val errorMessage: String? = null,
    val reducedSize: Long? = null,
    val error: String? = null
)
