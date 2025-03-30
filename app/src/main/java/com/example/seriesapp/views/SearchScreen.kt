package com.example.seriesapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.viewModel.SearchViewModel
import com.example.seriesapp.views.components.ShowListItem

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = viewModel { SearchViewModel(FavoritesRepository()) }
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is SearchViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SearchViewModel.State.Success -> {
            val successState = state as SearchViewModel.State.Success
            SearchContent(
                state = successState,
                onEvent = viewModel::handleEvent,
                navController = navController
            )
        }
    }
}

@Composable
private fun SearchContent(
    state: SearchViewModel.State.Success,
    onEvent: (SearchViewModel.Event) -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = state.searchText,
            onValueChange = { onEvent(SearchViewModel.Event.SearchTextChanged(it)) },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text("Min seasons: ${state.minSeasons}")
        Slider(
            value = state.minSeasons.toFloat(),
            onValueChange = { onEvent(SearchViewModel.Event.MinSeasonsChanged(it.toInt())) },
            valueRange = 1f..10f,
            steps = 9
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.sortByRating,
                onCheckedChange = { onEvent(SearchViewModel.Event.SortByRatingToggled(it)) }
            )
            Text("Sort by rating")
        }

        LazyColumn {
            items(state.filteredShows) { show ->
                ShowListItem(
                    show = show,
                    navController = navController,
                    onFavoriteClick = { onEvent(SearchViewModel.Event.ToggleFavorite(it)) }
                )
            }
        }
    }
}