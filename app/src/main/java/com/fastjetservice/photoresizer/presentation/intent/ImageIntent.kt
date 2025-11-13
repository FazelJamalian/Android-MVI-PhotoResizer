package com.fastjetservice.photoresizer.presentation.intent

import android.net.Uri

sealed class ImageIntent {
    data class PickImage(val uri: Uri) : ImageIntent()
    data class SetWidth(val width: Int) : ImageIntent()
    data class SetHeight(val height: Int) : ImageIntent()
    data class SetQuality(val quality: Int) : ImageIntent()
    data class SetFormat(val format: String) : ImageIntent()
    data class SetSize(val size: Int) : ImageIntent()
    data class Compress(val uri: Uri) : ImageIntent()
    data class ClearError(val message: String) : ImageIntent()
    object Reset : ImageIntent()
}
