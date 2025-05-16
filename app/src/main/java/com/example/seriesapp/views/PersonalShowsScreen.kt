package com.example.seriesapp.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seriesapp.db.PersonalShowsViewModel
import com.example.seriesapp.models.TvShow
import androidx.compose.foundation.lazy.items
import com.example.seriesapp.views.components.AddOrEditShowDialog
import com.example.seriesapp.views.components.ShowListItem
import androidx.compose.ui.res.stringResource
import com.example.seriesapp.R

@Composable
fun PersonalShowsScreen(
    viewModel: PersonalShowsViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val shows by viewModel.allShows.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editingShow by remember { mutableStateOf<TvShow?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (shows.isEmpty()) {
            Text(
                text = stringResource(R.string.add_series_prompt),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
                items(shows) { show ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        ShowListItem(
                            show = show,
                            navController = navController,
                            onFavoriteClick = { viewModel.toggleFavorite(it) }
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = {
                                editingShow = show
                                showDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit)
                                )
                            }
                            IconButton(onClick = {
                                viewModel.deleteShow(show)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.deleted),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingShow = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_series)
            )
        }
    }

    if (showDialog) {
        AddOrEditShowDialog(
            initialShow = editingShow,
            onDismiss = { showDialog = false },
            onSave = { newShow ->
                if (editingShow == null) {
                    viewModel.addShow(newShow)
                    Toast.makeText(
                        context,
                        context.getString(R.string.added),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.updateShow(newShow)
                    Toast.makeText(
                        context,
                        context.getString(R.string.updated),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                showDialog = false
            }
        )
    }
}