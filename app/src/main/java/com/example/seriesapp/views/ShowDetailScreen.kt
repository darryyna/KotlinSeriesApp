package com.example.seriesapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.seriesapp.viewModel.ShowDetailViewModel
import com.example.seriesapp.R
import androidx.compose.ui.res.stringResource

@Composable
fun ShowDetailScreen(
    showId: Int,
    navController: NavController,
    viewModel: ShowDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(showId) {
        viewModel.loadShow()
    }

    state.show?.let { show ->
        val imageResId = remember(show.imageName) {
            context.resources.getIdentifier(show.imageName, "drawable", context.packageName)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
                .testTag("showDetailScreen")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = show.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 0f,
                                endY = 500f
                            )
                        )
                )

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .testTag("backButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = show.title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("showTitle")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = show.genre,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp,
                            modifier = Modifier.testTag("showGenre")
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = show.rating.toString(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("showRating")
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleFavorite() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .testTag("favoriteButton")
                ) {
                    Icon(
                        imageVector = if (show.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (show.isFavorite)
                            stringResource(R.string.remove_from_favorites)
                        else
                            stringResource(R.string.add_to_favorites),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.testTag("favoriteIcon")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("watchingProgressCard"),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.watching_progress),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.seasons_progress,
                                    show.seasonsWatched,
                                    show.totalSeasons
                                ),
                                fontSize = 16.sp,
                                modifier = Modifier.testTag("seasonsWatchedText")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.LightGray)
                                    .testTag("seasonsProgressBarBackground")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(show.seasonsWatched.toFloat() / show.totalSeasons)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .testTag("seasonsProgressBar")
                                )
                            }
                        }

                        if (show.nextEpisodeDate != null) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(12.dp)
                                    .testTag("nextEpisodeRow")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(R.string.next_episode_detail, show.nextEpisodeDate),
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.testTag("nextEpisodeText")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("seriesInfoCard"),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.series_information),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.genre_detail),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = show.genre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.testTag("infoGenreText")
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.rating_detail),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.Yellow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(R.string.rating_out_of, show.rating),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.testTag("infoRatingText")
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.total_seasons),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${show.totalSeasons}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.testTag("infoTotalSeasonsText")
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.status),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = if (show.nextEpisodeDate != null)
                                        stringResource(R.string.ongoing)
                                    else
                                        stringResource(R.string.completed),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (show.nextEpisodeDate != null)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.testTag("infoStatusText")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.markSeasonWatched() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("markWatchedButton"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(stringResource(R.string.mark_watched), fontSize = 16.sp)
                        }
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .testTag("addNoteButton"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(stringResource(R.string.add_note), fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize().testTag("loadingOrErrorContainer"),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
            } else {
                Text(stringResource(R.string.error_loading), modifier = Modifier.testTag("errorText"))
            }
        }
    }
}