package com.fastjetservice.photoresizer.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.fastjetservice.photoresizer.data.FFmpegImageCompressor
import com.fastjetservice.photoresizer.presentation.ui.intent.EditIntent
import com.fastjetservice.photoresizer.presentation.viewmodel.EditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(viewModel: EditViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.handleIntent(EditIntent.PickImage(it)) }
        }
    )

    LaunchedEffect(state.error, state.compressedUri) {
        state.error?.let {
            snackbarHostState.showSnackbar(
                message = "خطا: $it",
                duration = SnackbarDuration.Long
            )
            viewModel.handleIntent(EditIntent.ClearError(""))
        }
        state.compressedUri?.let { uri ->
            val result = snackbarHostState.showSnackbar(
                message = "فایل با موفقیت ذخیره شد",
                actionLabel = "باز کردن",
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "image/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(viewIntent, "باز کردن با..."))
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("فشرده‌ساز عکس") },
                actions = {
                    IconButton(
                        onClick = { viewModel.handleIntent(EditIntent.Reset) },
                        enabled = state.imageUri != null
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "ریست")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = state.imageUri,
                        contentDescription = "تصویر انتخاب شده",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        },
                        error = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("یک عکس انتخاب کنید", textAlign = TextAlign.Center)
                            }
                        }
                    )
                }

                Spacer(Modifier.height(8.dp))
                state.originalWidth?.let {
                    Text(
                        "طول: ${state.originalWidth} عرض: ${state.originalHeight}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(visible = state.imageUri != null) {
                    Column {
                        OptionsPanel(viewModel)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(if (state.imageUri == null) "انتخاب عکس" else "تغییر عکس")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { state.imageUri?.let { viewModel.handleIntent(EditIntent.Compress(it)) } },
                    enabled = state.imageUri != null && !state.isResizing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Compress,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("فشرده‌سازی و ذخیره")
                }

                Spacer(Modifier.height(24.dp))
            }

            if (state.isResizing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text("در حال پردازش...", color = Color.White)
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ImageScreenPreview() {
    val fakeViewModel = EditViewModel(
        ffmpegCompressor = FFmpegImageCompressor(LocalContext.current),
        context = LocalContext.current
    )
    ImageScreen(fakeViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsPanel(viewModel: EditViewModel) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("حجم اصلی", style = MaterialTheme.typography.bodyLarge)
            state.originalSize?.let {
                Text(
                    Formatter.formatShortFileSize(context, it),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("حجم تخمینی", style = MaterialTheme.typography.bodyLarge)
            state.reducedSize?.let {
                Text(
                    Formatter.formatShortFileSize(context, it),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = state.width.toString(),
            onValueChange = { viewModel.handleIntent(EditIntent.SetWidth(it.toIntOrNull() ?: 0)) },
            label = { Text("عرض (px)") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = state.height.toString(),
            onValueChange = {
                viewModel.handleIntent(
                    EditIntent.SetHeight(
                        it.toIntOrNull() ?: 0
                    )
                )
            },
            label = { Text("ارتفاع (px)") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }

    Spacer(Modifier.height(16.dp))

    val isPng = state.format.equals("png", ignoreCase = true)

    Text(
        text = if (isPng) "کیفیت (برای PNG اعمال نمی شود)" else "کیفیت: ${state.quality}%",
        style = MaterialTheme.typography.bodyLarge,
        color = if (isPng) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else Color.Unspecified
    )
    Slider(
        value = state.quality.toFloat(),
        onValueChange = { viewModel.handleIntent(EditIntent.SetQuality(it.toInt())) },
        valueRange = 1f..100f,
        steps = 98,
        enabled = !isPng
    )

    Spacer(Modifier.height(16.dp))

    val formats = listOf("jpg", "png", "webp")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = state.format.uppercase(),
            onValueChange = {},
            readOnly = true,
            label = { Text("فرمت خروجی") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            formats.forEach { format ->
                DropdownMenuItem(
                    text = { Text(format.uppercase()) },
                    onClick = {
                        viewModel.handleIntent(EditIntent.SetFormat(format))
                        expanded = false
                    }
                )
            }
        }
    }
}
