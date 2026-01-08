package com.fastjetservice.photoresizer.presentation.ui.intent

import android.net.Uri

sealed class HomeIntent {
    data class PickImage(val uri: Uri) : HomeIntent()
}
