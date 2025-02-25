package com.grogolden.mullet.ui.components.tasks

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.grogolden.mullet.data.models.Mode
import com.grogolden.mullet.data.models.Task

/**
 * A UI component that displays an individual task.
 *
 * - Shows task **title** with a **mode-based border color**.
 * - Includes a **checkbox** to mark tasks as completed.
 * - Clickable to allow editing.
 *
 * @param task The task data to be displayed.
 * @param availableModes List of available modes to determine task's border color.
 * @param onTaskCheckedChange Callback triggered when the checkbox state changes.
 * @param onTaskClicked Callback triggered when the task card is clicked.
 */
@Composable
fun TaskCard(
    task: Task,
    availableModes: List<Mode>,
    onTaskCheckedChange: (Boolean) -> Unit,
    onTaskClicked: () -> Unit,
) {
    // Extract mode color based on task's modeId, default to gray
    val modeColor = availableModes.find { it.localId == task.modeId }?.color
    val borderColor = try {
        modeColor?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Gray
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onTaskClicked() }
            .border(
                width = 4.dp,
                color = borderColor.copy(alpha = 0.50f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { isChecked -> onTaskCheckedChange(isChecked) }
            )
        }
    }
}
