package com.fastjetservice.photoresizer.presentation.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fastjetservice.photoresizer.presentation.ui.intent.EditIntent
import com.fastjetservice.photoresizer.presentation.ui.navigation.Screens
import com.fastjetservice.photoresizer.presentation.viewmodel.EditViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController, viewModel: EditViewModel = hiltViewModel()) {

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.handleIntent(EditIntent.PickImage(it))
                navController.navigate(Screens.EditScreen.paramsWithArgs(uri.toString())

                )
            }
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(color = Color(0xFFFDFDFD))
                    .padding(WindowInsets.statusBars.asPaddingValues())
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(Color(0xFF155CFA), shape = RoundedCornerShape(12.dp))
                            .padding(6.dp)
                    ) {
                        Icon(
                            Icons.Default.ZoomInMap,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = "Media Compressor", fontSize = 16.sp, modifier = Modifier)
                        Text(
                            text = "Offline . Local processing",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier
                        )

                    }
                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(0.7.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(color = Color(0xFFF7F8F9)),
            contentAlignment = Alignment.Center
        ) {
            // 1. Set up the animation and coroutine scope
            val coroutineScope = rememberCoroutineScope()
            val scale = remember { Animatable(1f) }

            OutlinedCard(
                onClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitFirstDown(requireUnconsumed = false)
                                coroutineScope.launch {
                                    scale.animateTo(
                                        targetValue = 0.97f,
                                        animationSpec = tween(
                                            durationMillis = 100
                                        )
                                    )
                                }
                                waitForUpOrCancellation()
                                coroutineScope.launch {
                                    scale.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(
                                            durationMillis = 100
                                        )
                                    )
                                }
                            }
                        }
                    },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                border = BorderStroke(
                    color = Color(0xFFE0E0E0),
                    width = 1.dp,
                ),

                ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.height(30.dp))

                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .background(color = Color(0xFFD9E8FC), shape = RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.FolderOpen,
                            modifier = Modifier
                                .width(55.dp)
                                .height(55.dp),
                            tint = Color(0xFF155CFA),
                            contentDescription = "file"
                        )

                    }
                    Spacer(Modifier.height(30.dp))
                    Text(
                        text = "Select Media File", fontSize = 16.sp, modifier = Modifier
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Tap to browse your gallery",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                    )
                    Spacer(Modifier.height(30.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 30.dp)
                            .border(
                                1.dp,
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = Color(0xFFF5F6F7), shape = RoundedCornerShape(10.dp)
                            ), contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(Modifier.width(10.dp))
                            Icon(Icons.Outlined.Photo, contentDescription = "photo")
                            Spacer(Modifier.width(10.dp))
                            Text(text = "Photos (JPG, PNG, WebP, ... )")
                        }
                    }
                    /*Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 30.dp)
                            .border(
                                1.dp,
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = Color(0xFFF5F6F7), shape = RoundedCornerShape(10.dp)
                            ), contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(Modifier.width(10.dp))
                            Icon(Icons.Outlined.Videocam, contentDescription = "photo")
                            Spacer(Modifier.width(10.dp))
                            Text(text = "Videos (MP4, WebM)")
                        }
                    }*/
                    Spacer(Modifier.height(30.dp))

                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, bottom = 30.dp)
                    .background(color = Color(0xFFE6FFED), shape = RoundedCornerShape(16.dp))
                    .border(1.dp, color = Color(0xFF81C784), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "Lock",
                        tint = Color(0xFF1B5E20)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "All processing happens locally on your device",
                        color = Color(0xFF1B5E20),
                        fontSize = 14.sp
                    )
                }
            }

        }
    }

}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7_PRO, apiLevel = 36)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = NavHostController(LocalContext.current))
}
