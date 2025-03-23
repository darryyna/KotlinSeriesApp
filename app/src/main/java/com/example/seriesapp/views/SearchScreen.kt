package com.example.seriesapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.views.components.ShowListItem

@Composable
fun SearchScreen(
    navController: NavController,
    shows: List<TvShow>,
    toggleFavorite: (Int) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var minSeasons by remember { mutableFloatStateOf(0f) }
    var sortByRating by remember { mutableStateOf(false) }

    val filteredShows = shows.filter { show ->
        (show.title.contains(searchText, ignoreCase = true)) &&
                (show.totalSeasons >= minSeasons.toInt())
    }.let { filtered ->
        if (sortByRating) {
            filtered.sortedByDescending { it.rating }
        } else {
            filtered
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search by title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Min seasons: ${minSeasons.toInt()}", fontSize = 16.sp)
        Slider(
            value = minSeasons,
            onValueChange = { minSeasons = it },
            valueRange = 1f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = sortByRating,
                onCheckedChange = { sortByRating = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sort by highest rating", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredShows) { show ->
                ShowListItem(
                    show = show,
                    navController = navController,
                    toggleFavorite = toggleFavorite
                )
            }
        }
    }
}