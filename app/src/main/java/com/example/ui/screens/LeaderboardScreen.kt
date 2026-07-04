package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel

@Composable
fun LeaderboardScreen(
    viewModel: CompEnglishViewModel,
    modifier: Modifier = Modifier
) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    LaunchedEffect(Unit) {
        while(true) {
            viewModel.fetchLeaderboard()
            kotlinx.coroutines.delay(5000) // Poll every 5 seconds
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeBg)
    ) {
        // App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = ThemeTextDark
            )
        }

        HorizontalDivider(color = ThemeBorder.copy(alpha = 0.5f))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (leaderboard.isEmpty()) {
                item {
                    Text(
                        text = "Loading leaderboard or no data available...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeTextGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(leaderboard) { index, student ->
                    val isTop3 = index < 3
                    val rankColor = when (index) {
                        0 -> Color(0xFFFFD700) // Gold
                        1 -> Color(0xFFC0C0C0) // Silver
                        2 -> Color(0xFFCD7F32) // Bronze
                        else -> ThemeTextGray
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, ThemeBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isTop3) rankColor.copy(alpha = 0.1f) else ThemeIconBox),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = rankColor
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Student ${student.studentCode}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = ThemeTextDark
                                )
                                Text(
                                    text = "Best: ${student.bestScore} | Avg Time: ${student.avgTimeTaken}s",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ThemeTextGray
                                )
                            }

                            Text(
                                text = "${student.avgAccuracy}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Purple40
                            )
                        }
                    }
                }
            }
        }
    }
}
