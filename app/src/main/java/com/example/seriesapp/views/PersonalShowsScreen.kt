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
                text = "Додайте серіали у свій список!",
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
                            IconButton(onClick = { editingShow = show; showDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Редагувати")
                            }
                            IconButton(onClick = {
                                viewModel.deleteShow(show)
                                Toast.makeText(context, "Видалено", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Видалити")
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
            Icon(Icons.Default.Add, contentDescription = "Додати серіал")
        }
    }

    if (showDialog) {
        AddOrEditShowDialog(
            initialShow = editingShow,
            onDismiss = { showDialog = false },
            onSave = { newShow ->
                if (editingShow == null) {
                    viewModel.addShow(newShow)
                    Toast.makeText(context, "Додано", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateShow(newShow)
                    Toast.makeText(context, "Оновлено", Toast.LENGTH_SHORT).show()
                }
                showDialog = false
            }
        )
    }
}