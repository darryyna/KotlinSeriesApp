package com.example.seriesapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.views.components.ShowRecommendedGridItem

@Composable
fun RecommendationsScreen(
    navController: NavController,
    recommendedShowsByGenre: Map<String, List<TvShow>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recommended for You",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            recommendedShowsByGenre.forEach { (genre, shows) ->
//                item {
//                    Text(
//                        text = genre,
//                        style = MaterialTheme.typography.titleMedium,
//                        color = MaterialTheme.colorScheme.primary
//
//                    )
//                }
//
//                item {
//                    LazyRow(
//                        horizontalArrangement = Arrangement.spacedBy(12.dp),
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        items(shows) { show ->
//                            ShowRecommendedGridItem(show, navController)
//                        }
//                    }
//                }
//            }
//        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recommendedShowsByGenre.values.flatten()) { show ->
                ShowRecommendedGridItem(show, navController)
            }
        }
    }
}
