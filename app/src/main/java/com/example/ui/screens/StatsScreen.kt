package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ActivityItem
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel

@Composable
fun StatsScreen(
    viewModel: CompEnglishViewModel,
    modifier: Modifier = Modifier
) {
    val studentAttempts by viewModel.studentAttempts.collectAsState()
    val availableTests by viewModel.availableTests.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStudentAttempts()
    }

    // Calculate real stats from Supabase attempts
    val activities = remember(studentAttempts, availableTests) {
        studentAttempts.map { attempt ->
            val correspondingTest = availableTests.firstOrNull { it.id == attempt.testId.toString() }
            ActivityItem(
                id = 0,
                title = correspondingTest?.title ?: "Mock Test ${attempt.testId}",
                type = "Completed",
                category = correspondingTest?.category ?: "Reading",
                score = attempt.accuracy,
                totalQuestions = correspondingTest?.questionsCount ?: 20,
                timeSpentSec = attempt.timeTaken,
                timestamp = try {
                    java.time.Instant.parse(attempt.completedAt ?: "").toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                },
                iconEmoji = correspondingTest?.iconEmoji ?: "📝"
            )
        }
    }

    val completedTests = activities

    val totalCompleted = completedTests.size
    val averageScore = remember(completedTests) {
        if (completedTests.isNotEmpty()) {
            completedTests.map { it.score }.average().toInt()
        } else {
            0
        }
    }

    val streakDays = remember(activities) {
        // Simple streak: number of unique days of activities
        activities.map { 
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            sdf.format(java.util.Date(it.timestamp))
        }.distinct().size
    }

    // Category breakdown scores
    val grammarAvg = remember(completedTests) {
        val list = completedTests.filter { it.category.equals("Grammar", ignoreCase = true) }
        if (list.isNotEmpty()) list.map { it.score }.average().toInt() else 0
    }
    val vocabAvg = remember(completedTests) {
        val list = completedTests.filter { it.category.equals("Vocabulary", ignoreCase = true) }
        if (list.isNotEmpty()) list.map { it.score }.average().toInt() else 0
    }
    val speakingAvg = remember(completedTests) {
        val list = completedTests.filter { it.category.equals("Speaking", ignoreCase = true) }
        if (list.isNotEmpty()) list.map { it.score }.average().toInt() else 0
    }
    val readingAvg = remember(completedTests) {
        val list = completedTests.filter { it.category.equals("Reading", ignoreCase = true) }
        if (list.isNotEmpty()) list.map { it.score }.average().toInt() else 0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Title & Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Statistics",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 32.sp
                        ),
                        color = ThemeTextDark,
                        modifier = Modifier.testTag("stats_title")
                    )
                    Text(
                        text = "Real-time analysis of mock test attempts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeTextGray
                    )
                }

                if (activities.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearHistory() },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear all history",
                            tint = Color(0xFFB3261E)
                        )
                    }
                }
            }
        }

        // 3 core stat cards row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Avg Score",
                    value = "$averageScore%",
                    subtitle = "All Mock Tests",
                    color = Purple40,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Completed",
                    value = "$totalCompleted",
                    subtitle = "Tests Completed",
                    color = ThemeDarkPurple,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Study Streak",
                    value = "$streakDays",
                    subtitle = "Active Days",
                    color = Color(0xFFB3261E),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Canvas performance chart card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("performance_chart_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ThemeBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "CATEGORY PERFORMANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ThemeTextGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw our performance bar chart using Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        PerformanceBarChart(
                            scores = listOf(grammarAvg, vocabAvg, speakingAvg, readingAvg),
                            labels = listOf("Grammar", "Vocab", "Speaking", "Reading")
                        )
                    }
                }
            }
        }

        // Circular Goal Progress
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_target_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ThemeBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Track circle
                            drawCircle(
                                color = ThemeBorder.copy(alpha = 0.4f),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Target sweep arc
                            drawArc(
                                color = Purple40,
                                startAngle = -90f,
                                sweepAngle = (averageScore * 3.6f).coerceAtMost(360f).toFloat(),
                                useCenter = false,
                                size = Size(size.width, size.height),
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "$averageScore%",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ThemeTextDark
                        )
                    }

                    Column {
                        Text(
                            text = "Accuracy Goal Target",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ThemeTextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You are currently achieving $averageScore% accuracy. Keep training to hit your target IELTS 8.0 band level!",
                            style = MaterialTheme.typography.bodySmall,
                            color = ThemeTextGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ThemeBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = ThemeTextGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = ThemeTextGray
            )
        }
    }
}

@Composable
fun PerformanceBarChart(
    scores: List<Int>,
    labels: List<String>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val barCount = scores.size
        val barWidth = 40.dp.toPx()
        val spacing = (size.width - (barWidth * barCount)) / (barCount + 1)
        val maxScoreHeight = size.height - 40.dp.toPx()

        // Horizontal baseline
        drawLine(
            color = ThemeBorder,
            start = Offset(0f, size.height - 24.dp.toPx()),
            end = Offset(size.width, size.height - 24.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )

        for (i in 0 until barCount) {
            val score = scores[i]
            val barHeight = (score / 100f) * maxScoreHeight
            val xOffset = spacing + i * (barWidth + spacing)
            val yOffset = size.height - 24.dp.toPx() - barHeight

            // Draw shadow track background
            drawRoundRect(
                color = ThemeIconBox,
                topLeft = Offset(xOffset, size.height - 24.dp.toPx() - maxScoreHeight),
                size = Size(barWidth, maxScoreHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Draw score filled bar
            drawRoundRect(
                color = if (score > 0) Purple40 else ThemeBorder,
                topLeft = Offset(xOffset, yOffset),
                size = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Write scores on top of bars
            if (score > 0) {
                // Compose draws custom text inside Canvas, but since standard Compose Canvas text drawing
                // requires Native Canvas or TextMeasurer which might add complexity, we can let the scores speak for themselves,
                // or draw beautiful indicators. To make it extremely safe, we draw a little white accent circle in the bar.
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(xOffset + barWidth / 2, yOffset + 10.dp.toPx())
                )
            }
        }
    }

    // Overlaying text labels underneath
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 156.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        labels.forEachIndexed { idx, label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${scores[idx]}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Purple40
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ThemeTextGray
                )
            }
        }
    }
}
