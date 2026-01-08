package com.fastjetservice.photoresizer.presentation.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ZoomInMap
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.fastjetservice.photoresizer.data.FFmpegImageCompressor
import com.fastjetservice.photoresizer.presentation.ui.components.CompressionSettingsSheet
import com.fastjetservice.photoresizer.presentation.ui.intent.EditIntent
import com.fastjetservice.photoresizer.presentation.viewmodel.EditViewModel
import com.fastjetservice.photoresizer.utils.toReadableFileSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    viewModel: EditViewModel = hiltViewModel(),
    uri: Uri?
) {

    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var pagerState by remember { mutableIntStateOf(0) }

    uri?.let {
        viewModel.handleIntent(EditIntent.PickImage(it))
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.background(color = Color(0xFFFDFDFD))) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(16.dp))
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
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDFDFD))
            ) {
                if (state.compressedUri != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF155CFA), Color(0xFF9C27B0))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Size Reduced:", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            "${state.reducedSize?.toReadableFileSize() ?: "0 KB"} saved",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (!showBottomSheet) showBottomSheet = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Settings")
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = { viewModel.handleIntent(EditIntent.SaveToPhoto(state.compressedUri!!)) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier

                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF155CFA), Color(0xFF9C27B0))
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = "Save",
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Save to Device",
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(16.dp))

                    OutlinedIconButton(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.LightGray
                        ),
                        modifier = Modifier,
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Outlined.Share,
                                contentDescription = "Share",

                                )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(color = Color(0xFFF7F8F9)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            if (pagerState == 0) "Original: ${state.originalSize?.toReadableFileSize() ?: "0 KB"}"
                            else if (pagerState == 1 && state.compressedUri != null) "Compressed: ${state.compressedSize?.toReadableFileSize() ?: "0 KB"}"
                            else "Compressed: 0 KB",
                            color = Color(0xFF155CFA),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (state.compressedSize != null && state.originalSize != null) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = "Reduced by",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))


                                val original = state.originalSize!!.toDouble()
                                val compressed = state.compressedSize!!.toDouble()
                                val reductionPercentage =
                                    ((original - compressed) / original * 100).toInt()


                                Text(//18/130*100
                                    text = "$reductionPercentage%",
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEEEEEE)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = if (pagerState == 0) {
                            Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        } else {
                            Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable(onClick = { pagerState = 0 })
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Original",
                            fontWeight = if (pagerState == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (pagerState == 0) Color.Black else Color.Gray
                        )
                    }
                    Box(
                        modifier = if (pagerState == 1) {
                            Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        } else {
                            Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable(onClick = { pagerState = 1 })
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Compressed",
                            fontWeight = if (pagerState == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (pagerState == 1) Color.Black else Color.Gray
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)

                ) {
                    SubcomposeAsyncImage(
                        model = if (pagerState == 0) state.imageUri else state.compressedUri,
                        contentDescription = "تصویر انتخاب شده",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(0xFFEEEEEE)),
                        contentScale = ContentScale.FillWidth,
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(72.dp)
                                        .size(48.dp),
                                    color = Color(0xFF155CFA)
                                )
                            }
                        },
                        error = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color = Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("First Compress the Media !!", textAlign = TextAlign.Center)
                            }
                        }
                    )
                }
                Spacer(Modifier.height(16.dp))

            }
            if (showBottomSheet) {

                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    CompressionSettingsSheet(
                        state = state,
                        onCompressMedia = {
                            viewModel.handleIntent(EditIntent.Compress(state.imageUri!!))
                        },
                        onWidthChange = {
                            viewModel.handleIntent(EditIntent.SetWidth(it))
                        },
                        onHeightChange = {
                            viewModel.handleIntent(EditIntent.SetHeight(it))
                        },
                        onFormatChange = {
                            viewModel.handleIntent(EditIntent.SetFormat(it))
                        },
                        onSliderValueChange = {
                            viewModel.handleIntent(EditIntent.SetQuality(it.toInt()))
                        },
                        onDismiss = { showBottomSheet = false }
                    )
                }
            }
        }

    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = false, device = Devices.PIXEL_7_PRO)
@Composable
fun EditScreenPreview() {
    EditScreen(
        viewModel = EditViewModel(
            FFmpegImageCompressor(LocalContext.current),
            LocalContext.current
        ), uri = null, navController = NavController(LocalContext.current)
    )
}
