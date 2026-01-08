package com.fastjetservice.photoresizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fastjetservice.photoresizer.presentation.ui.navigation.NavGraph
import com.fastjetservice.photoresizer.presentation.ui.theme.PhotoResizerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoResizerTheme {
                NavGraph()
            }
        }
    }
}
