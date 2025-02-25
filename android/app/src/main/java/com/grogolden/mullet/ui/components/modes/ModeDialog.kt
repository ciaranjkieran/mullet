package com.grogolden.mullet.ui.components.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

import com.grogolden.mullet.data.models.Mode

/**
 * A dialog for adding, editing, or deleting a mode.
 *
 * - If `modeToEdit` is null, the dialog creates a new mode.
 * - Otherwise, the dialog updates the existing mode.
 * - Users can set the mode name and pick a color.
 *
 * @param modeToEdit The mode being edited, or null for a new mode.
 * @param onDismiss Callback triggered when the dialog is closed.
 * @param onModeAdded Callback triggered when a new mode is added.
 * @param onModeUpdated Callback triggered when an existing mode is updated.
 * @param onModeDeleted Callback triggered when a mode is deleted.
 */
@Composable
fun ModeDialog(
    modeToEdit: Mode?,
    onDismiss: () -> Unit,
    onModeAdded: (Mode) -> Unit,
    onModeUpdated: (Mode) -> Unit,
    onModeDeleted: (Mode) -> Unit,
) {
    var modeTitle by remember { mutableStateOf(modeToEdit?.title ?: "") }
    var selectedColor by remember {
        mutableStateOf(
            Color(
                android.graphics.Color.parseColor(
                    modeToEdit?.color ?: "#000000"
                )
            )
        )
    }
    var showColorPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (modeToEdit == null) "Add Mode" else "Edit Mode") },
        text = {
            Column {
                // Mode Name Input Field
                OutlinedTextField(
                    value = modeTitle,
                    onValueChange = { modeTitle = it },
                    label = { Text("Mode Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Open Color Picker Dialog
                Button(onClick = { showColorPicker = true }) {
                    Text("Pick Color")
                }

                if (showColorPicker) {
                    ColorPickerDialog(
                        initialColor = selectedColor,
                        onColorSelected = { color ->
                            selectedColor = color
                            showColorPicker = false
                        },
                        onDismiss = { showColorPicker = false }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newMode = modeToEdit?.copy(
                        title = modeTitle,
                        color = String.format("#%06X", (0xFFFFFF and selectedColor.toArgb())),
                        isSynced = false
                    ) ?: Mode(
                        localId = 0,
                        id = null,
                        title = modeTitle,
                        color = String.format("#%06X", (0xFFFFFF and selectedColor.toArgb())),
                        isSynced = false
                    )

                    if (modeToEdit == null) {
                        onModeAdded(newMode)
                    } else {
                        onModeUpdated(newMode)
                    }
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (modeToEdit != null) {
                    Button(
                        onClick = {
                            onModeDeleted(modeToEdit)
                            onDismiss()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Delete")
                    }
                }
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
