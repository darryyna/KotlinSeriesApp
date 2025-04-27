package com.example.seriesapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seriesapp.R

data class NewsItem(
    val id: Int,
    val title: String,
    val description: String,
    val source: String,
    val date: String,
    val imageResId: Int
)

val mockNewsItems = listOf(
    NewsItem(
        id = 1,
        title = "Анонсовано новий сезон 'Відьмака'",
        description = "Netflix підтвердив четвертий сезон із новим актором на роль Геральта.",
        source = "ScreenRant",
        date = "27 квітня 2025",
        imageResId = R.drawable.st
    ),
    NewsItem(
        id = 2,
        title = "Зірка 'Дивних див' приєднується до Marvel",
        description = "Міллі Боббі Браун зіграє ключову роль у новому серіалі MCU.",
        source = "Variety",
        date = "26 квітня 2025",
        imageResId = R.drawable.st
    ),
    NewsItem(
        id = 3,
        title = "Продовження 'Гри престолів' у 2026",
        description = "HBO розкриває деталі про спін-офф 'Дім Дракона'.",
        source = "Deadline",
        date = "25 квітня 2025",
        imageResId = R.drawable.st
    ),
    NewsItem(
        id = 4,
        title = "Новий серіал від творців 'Чорнобиля'",
        description = "HBO анонсує історичну драму про середньовіччя.",
        source = "Hollywood Reporter",
        date = "24 квітня 2025",
        imageResId = R.drawable.st
    )
)

@Composable
fun DiscoveryScreen() {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val isTablet = screenWidthDp >= 600.dp
    val columns = if (isTablet) 2 else 1
    val cardPadding = if (isTablet) 24.dp else 16.dp
    val cardMaxWidth = if (isTablet) 400.dp else screenWidthDp - 32.dp
    val headerFontSize = if (isTablet) 32.sp else 28.sp

    Text(
        text = "Новини серіалів",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        fontSize = headerFontSize,
        modifier = Modifier
            .padding(horizontal = cardPadding, vertical = 16.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary
    )

    if (columns == 1) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = cardPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(mockNewsItems) { item ->
                NewsCard(
                    item = item,
                    maxWidth = cardMaxWidth,
                    isTablet = isTablet
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = cardPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            gridItems(mockNewsItems) { item ->
                NewsCard(
                    item = item,
                    maxWidth = cardMaxWidth,
                    isTablet = isTablet,
                )
            }
        }
    }
}

@Composable
fun NewsCard(
    item: NewsItem,
    maxWidth: Dp,
    isTablet: Boolean
) {
    val titleFontSize = if (isTablet) 20.sp else 18.sp
    val descriptionFontSize = if (isTablet) 16.sp else 14.sp
    val metaFontSize = if (isTablet) 14.sp else 12.sp
    val imageHeight = if (isTablet) 220.dp else 180.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = maxWidth),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = titleFontSize,
                lineHeight = titleFontSize * 1.2
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = descriptionFontSize,
                color = Color.Gray,
                lineHeight = descriptionFontSize * 1.3
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.source,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = metaFontSize,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = metaFontSize,
                    color = Color.Gray
                )
            }
        }
    }
}