package com.grogolden.mullet.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A floating action button (FAB) with a dropdown menu for adding tasks and modes.
 *
 * - Clicking the FAB opens a dropdown menu with two options:
 *   - "Add Task": Triggers the `onAddTask` callback.
 *   - "Add Mode": Triggers the `onAddMode` callback.
 *
 * @param onAddTask Callback function triggered when "Add Task" is selected.
 * @param onAddMode Callback function triggered when "Add Mode" is selected.
 */
@Composable
fun AddButton(
    onAddTask: () -> Unit,
    onAddMode: () -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { isMenuExpanded = true },
            modifier = Modifier
                .padding(16.dp)
                .size(72.dp)
                .offset(y = (-32).dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .width(180.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Add Task",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onAddTask()
                },
                modifier = Modifier
                    .padding(8.dp)
            )
            DropdownMenuItem(
                text = {
                    Text(
                        "Add Mode",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onAddMode()
                },
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}
