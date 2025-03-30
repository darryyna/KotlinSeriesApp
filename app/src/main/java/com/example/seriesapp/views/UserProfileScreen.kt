package com.example.seriesapp.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seriesapp.R
import com.example.seriesapp.viewModel.UserProfileViewModel
import com.example.seriesapp.views.components.SettingRow
import com.example.seriesapp.views.components.StatItem
import java.time.format.DateTimeFormatter

data class UserStats(
    val watched: Int,
    val inProgress: Int,
    val abandoned: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    val stats by viewModel.stats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = user?.name ?: "ноунейм",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    user?.birthDate?.let {
                        val formattedDate = it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        Text(
                            text = "Birth date: $formattedDate",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "View Statistics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(title = "Completed", count = stats.watched, color = Color(0xFF4CAF50))
                    StatItem(title = "In Progress", count = stats.inProgress, color = Color(0xFF2196F3))
                    StatItem(title = "Abandoned", count = stats.abandoned, color = Color(0xFFF44336))
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "App Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                var darkModeEnabled by remember { mutableStateOf(false) }
                SettingRow(
                    icon = Icons.Outlined.MoreVert,
                    title = "Dark Mode",
                    endContent = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SettingRow(
                    icon = Icons.Outlined.LocationOn,
                    title = "App Language",
                    description = "English",
                    showArrow = true
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SettingRow(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    description = "New episodes and recommendations",
                    showArrow = true
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Privacy & Security",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SettingRow(
                    icon = Icons.Outlined.Info,
                    title = "Change Password",
                    showArrow = true
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                SettingRow(
                    icon = Icons.Outlined.Settings,
                    title = "Privacy Settings",
                    description = "Data and permissions management",
                    showArrow = true
                )
            }
        }
    }
}

