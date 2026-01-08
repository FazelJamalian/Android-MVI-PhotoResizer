package com.fastjetservice.photoresizer.presentation.ui.intent

import android.net.Uri

sealed class EditIntent {
    data class PickImage(val uri: Uri) : EditIntent()
    data class CompressedImage(val uri: Uri) : EditIntent()
    data class Compress(val uri: Uri) : EditIntent()
    data class SaveToPhoto(val uri: Uri) : EditIntent()
    data class SetWidth(val width: Int) : EditIntent()
    data class SetHeight(val height: Int) : EditIntent()
    data class SetQuality(val quality: Int) : EditIntent()
    data class SetFormat(val format: String) : EditIntent()
    data class SetSize(val size: Int) : EditIntent()
    data class ClearError(val message: String) : EditIntent()
    object Reset : EditIntent()
}
