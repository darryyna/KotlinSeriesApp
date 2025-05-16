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
import androidx.compose.ui.res.stringResource
import com.example.seriesapp.R

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
                Text(stringResource(id = if (initialShow == null) R.string.add else R.string.update))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(id = if (initialShow == null) R.string.add_new_show else R.string.edit_show))
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = show.title,
                    onValueChange = { show = show.copy(title = it) },
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = show.imageName,
                    onValueChange = { show = show.copy(imageName = it) },
                    label = { Text(stringResource(R.string.image_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = show.genre,
                    onValueChange = { show = show.copy(genre = it) },
                    label = { Text(stringResource(R.string.genre)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = show.rating.toString(),
                    onValueChange = {
                        val newRating = it.toFloatOrNull() ?: 0f
                        show = show.copy(rating = newRating)
                    },
                    label = { Text(stringResource(R.string.rating)) },
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
                        label = { Text(stringResource(R.string.watched)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = show.totalSeasons.toString(),
                        onValueChange = {
                            val ts = it.toIntOrNull() ?: 1
                            show = show.copy(totalSeasons = ts)
                        },
                        label = { Text(stringResource(R.string.total)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = show.nextEpisodeDate ?: "",
                    onValueChange = { show = show.copy(nextEpisodeDate = it.ifBlank { null }) },
                    label = { Text(stringResource(R.string.next_episode)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = show.isFavorite,
                        onCheckedChange = { show = show.copy(isFavorite = it) }
                    )
                    Text(stringResource(R.string.mark_as_favorite))
                }
            }
        }
    )
}
