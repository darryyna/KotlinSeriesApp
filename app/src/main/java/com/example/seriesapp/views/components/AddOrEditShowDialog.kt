package com.example.seriesapp.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.seriesapp.models.TvShow

@Composable
fun AddOrEditShowDialog(
    initialShow: TvShow? = null,
    onDismiss: () -> Unit,
    onSave: (TvShow) -> Unit
) {
    var show by remember {
        mutableStateOf(
            initialShow ?: TvShow(
                title = "",
                imageName = "",
                genre = "",
                rating = 0f,
                seasonsWatched = 0,
                totalSeasons = 1
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (show.title.isNotBlank() && show.imageName.isNotBlank() && show.genre.isNotBlank()) {
                        onSave(show)
                    }
                }
            ) {
                Text(if (initialShow == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = if (initialShow == null) "Add New Show" else "Edit Show")
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = show.title,
                    onValueChange = { show = show.copy(title = it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = show.imageName,
                    onValueChange = { show = show.copy(imageName = it) },
                    label = { Text("Image name (drawable)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = show.genre,
                    onValueChange = { show = show.copy(genre = it) },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = show.rating.toString(),
                    onValueChange = {
                        val newRating = it.toFloatOrNull() ?: 0f
                        show = show.copy(rating = newRating)
                    },
                    label = { Text("Rating") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = show.seasonsWatched.toString(),
                        onValueChange = {
                            val sw = it.toIntOrNull() ?: 0
                            show = show.copy(seasonsWatched = sw)
                        },
                        label = { Text("Watched") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = show.totalSeasons.toString(),
                        onValueChange = {
                            val ts = it.toIntOrNull() ?: 1
                            show = show.copy(totalSeasons = ts)
                        },
                        label = { Text("Total") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = show.nextEpisodeDate ?: "",
                    onValueChange = { show = show.copy(nextEpisodeDate = it.ifBlank { null }) },
                    label = { Text("Next episode (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = show.isFavorite,
                        onCheckedChange = { show = show.copy(isFavorite = it) }
                    )
                    Text("Mark as favorite")
                }
            }
        }
    )
}
