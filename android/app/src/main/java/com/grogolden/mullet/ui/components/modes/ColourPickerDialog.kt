package com.grogolden.mullet.ui.components.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A color picker dialog that allows users to select from a predefined set of colors.
 *
 * - Displays a live preview of the selected color.
 * - Provides a grid of colors for selection.
 * - Users can confirm or cancel their selection.
 *
 * @param initialColor The starting color pre-selected in the picker.
 * @param onColorSelected Callback function triggered when a color is chosen.
 * @param onDismiss Callback function triggered when the dialog is closed.
 */
@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta,
        Color.Cyan, Color.Black, Color.Gray, Color.White, Color(0xFFFFA500), // Orange
        Color(0xFF8A2BE2), // BlueViolet
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a Color") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Live preview of the selected color
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(selectedColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Selection Grid (4 colors per row)
                Column {
                    colors.chunked(4).forEach { rowColors ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { selectedColor = color }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onColorSelected(selectedColor)
                onDismiss()
            }) {
                Text("Select")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
