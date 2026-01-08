package com.fastjetservice.photoresizer.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fastjetservice.photoresizer.domain.model.EditState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompressionSettingsSheet(
    state: EditState,
    onCompressMedia: () -> Unit,
    onWidthChange: (it: Int) -> Unit,
    onHeightChange: (it: Int) -> Unit,
    onFormatChange: (it: String) -> Unit,
    onSliderValueChange: (it: Float) -> Unit,
    onDismiss: () -> Unit
) {
    val state = state
    var width by remember { mutableStateOf(state.originalWidth) }
    var height by remember { mutableStateOf(state.originalHeight) }
    var quality by remember { mutableFloatStateOf(state.quality.toFloat()) }
    // var lockAspectRatio by remember { mutableStateOf(true) }
    var isFormatMenuExpanded by remember { mutableStateOf(false) }
    var selectedFormat by remember { mutableStateOf("JPG") } // Default format
    val formatOptions = listOf("JPG", "PNG", "WEBP")
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF155CFA), Color(0xFF9C27B0))
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Compression Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onDismiss() }) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        Text("All processing happens on your device", color = Color.Gray, fontSize = 12.sp)

        Spacer(Modifier.height(16.dp))
        /* Text("Quick Presets", fontWeight = FontWeight.Bold)
         Spacer(Modifier.height(8.dp))
         Row(
             modifier = Modifier.fillMaxWidth(),
             horizontalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             OutlinedButton(
                 modifier = Modifier.weight(1f),
                 shape = RoundedCornerShape(8.dp),
                 onClick = { *//**//* }) { Text("1080p") }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                onClick = { *//**//* }) { Text("720p") }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                onClick = { *//**//* }) { Text("480p") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                onClick = { *//**//* }) { Text("Instagram") }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                onClick = { *//**//* }) { Text("Story") }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                onClick = { *//**//* }) { Text("Original") }
        }

        Spacer(Modifier.height(16.dp))*/
        Text("Custom Dimensions", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = width?.toString() ?: "0",
                onValueChange = { newValue ->
                    val newWidth = newValue.trim().toIntOrNull()
                    width = newWidth
                    onWidthChange(width ?: 0)
                },
                label = { Text("Width (px)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = height?.toString() ?: "0",
                onValueChange = { newValue ->
                    val newHeight = newValue.trim().toIntOrNull()
                    height = newHeight
                    onHeightChange(height ?: 0)
                },
                label = { Text("Height (px)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        /* Spacer(Modifier.height(8.dp))
         Row(verticalAlignment = Alignment.CenterVertically) {
             GradientSwitch(
                 checked = lockAspectRatio,
                 onCheckedChange = { lockAspectRatio = it },
                 thumbColor = Color.White
             )
             Spacer(Modifier.width(8.dp))
             Text("Lock aspect ratio")
         }*/

        Spacer(Modifier.height(16.dp))
        Text("Output Format", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedFormat,
                onValueChange = {
                    selectedFormat = it
                    onFormatChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isFormatMenuExpanded = true }, // Open the menu on click
                readOnly = true,
                enabled = false, // Disables the text field's own cursor and interactions
                colors = OutlinedTextFieldDefaults.colors( // Manually set colors to look enabled
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Open output format menu"
                    )
                }
            )

            DropdownMenu(
                expanded = isFormatMenuExpanded,
                onDismissRequest = { isFormatMenuExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f) // Adjust width as needed
            ) {
                formatOptions.forEach { format ->
                    DropdownMenuItem(
                        text = { Text(format) },
                        onClick = {
                            onFormatChange(format.lowercase())
                            selectedFormat = format
                            isFormatMenuExpanded = false

                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Compression Quality", fontWeight = FontWeight.Bold)
            Text("${quality.toInt()}%")
        }
        Slider(
            value = quality,
            onValueChange = { quality = it; onSliderValueChange(quality) },
            valueRange = 0f..100f,
            track = { sliderState ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    val fraction = (sliderState.value - sliderState.valueRange.start) /
                            (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(8.dp)
                            .background(
                                brush = gradientBrush,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Smaller file", fontSize = 12.sp, color = Color.Gray)
            Text("Better quality", fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F4FF), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Original size:")
                    Text(
                        "${state.originalWidth} x ${state.originalHeight} px",
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("New size:")
                    Text(
                        "${state.compressedWidth ?: state.originalWidth} x ${state.compressedHeight ?: state.originalHeight} px",
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                onCompressMedia()
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF155CFA), Color(0xFF9C27B0))
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Compress Media",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
/*

@Composable
private fun GradientSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thumbColor: Color
) {
    val switchWidth = 52.dp
    val switchHeight = 32.dp
    val thumbDiameter = 24.dp
    val thumbPadding = (switchHeight - thumbDiameter) / 2

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) switchWidth - thumbDiameter - thumbPadding else thumbPadding,
        label = "thumbOffset"
    )

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .width(switchWidth)
            .height(switchHeight)
            .clip(RoundedCornerShape(switchHeight / 2))
            .background(if (checked) Color(0xFF155CFA) else Color(0xFF9C27B0))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbDiameter)
                .background(thumbColor, CircleShape)
        )
    }
}
*/


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = false, device = Devices.PIXEL_7_PRO)
@Composable
fun CompressionSettingsSheetPreview() {
    CompressionSettingsSheet(
        state = EditState(),
        onCompressMedia = {},
        onWidthChange = {},
        onHeightChange = {},
        onFormatChange = {},
        onSliderValueChange = {},
        onDismiss = {}
    )
}
