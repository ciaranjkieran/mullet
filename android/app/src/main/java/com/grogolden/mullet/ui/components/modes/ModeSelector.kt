package com.grogolden.mullet.ui.components.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.navigation.compose.hiltViewModel
import com.grogolden.mullet.viewmodel.GlobalModeViewModel

/**
 * A UI component for selecting and managing modes.
 *
 * - Displays the currently selected mode with a **colored underline**.
 * - Users can click the mode title to **select a different mode** from a dropdown.
 * - A three-dot menu allows **editing, deleting, or adding modes**.
 *
 * @param globalModeViewModel ViewModel that manages modes.
 * @param onEditMode Callback triggered when the edit mode option is selected.
 * @param onDeleteMode Callback triggered when the delete mode option is selected.
 * @param onAddMode Callback triggered when adding a new mode.
 */
@Composable
fun ModeSelector(
    globalModeViewModel: GlobalModeViewModel = hiltViewModel(),
    onEditMode: () -> Unit,
    onDeleteMode: () -> Unit,
    onAddMode: () -> Unit,
) {
    val modes by globalModeViewModel.modes.collectAsState()
    val selectedMode by globalModeViewModel.selectedMode.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isOptionsMenuExpanded by remember { mutableStateOf(false) }

    val modeColor = try {
        selectedMode?.let { Color(android.graphics.Color.parseColor(it.color)) } ?: Color.Gray
    } catch (e: Exception) {
        Color.Gray
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedMode?.title ?: "All",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = modeColor,
                modifier = Modifier
                    .clickable { isDropdownExpanded = true }
                    .weight(1f)
                    .padding(bottom = 4.dp)
            )

            IconButton(onClick = { isOptionsMenuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Mode Options")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(modeColor, RoundedCornerShape(2.dp))
        )

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            modes.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.title, fontWeight = FontWeight.Bold) },
                    onClick = {
                        globalModeViewModel.selectMode(mode)
                        isDropdownExpanded = false
                    }
                )
            }
        }

        DropdownMenu(
            expanded = isOptionsMenuExpanded,
            onDismissRequest = { isOptionsMenuExpanded = false }
        ) {
            if (selectedMode?.title == "All") {
                DropdownMenuItem(text = { Text("Add Mode") }, onClick = {
                    isOptionsMenuExpanded = false
                    onAddMode()
                })
            } else {
                DropdownMenuItem(text = { Text("Edit Mode") }, onClick = {
                    isOptionsMenuExpanded = false
                    onEditMode()
                })
                DropdownMenuItem(text = { Text("Delete Mode") }, onClick = {
                    isOptionsMenuExpanded = false
                    onDeleteMode()
                })
            }
        }
    }
}
