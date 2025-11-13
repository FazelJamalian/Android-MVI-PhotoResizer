package com.fastjetservice.photoresizer.domain.model

import android.net.Uri

data class ImageState(
    val width: Int = 1000,
    val height: Int = 1000,
    val quality: Int = 80,
    val format: String = "jpg",
    val imageUri: Uri? = null,
    val originalWidth: Int? = null,
    val originalHeight: Int? = null,
    val originalSize: Long? = null,
    val isResizing: Boolean = false,
    val errorMessage: String? = null,
    val estimatedSize: Long? = null,
    val outputFileUri: Uri? = null,
    val error: String? = null
)
