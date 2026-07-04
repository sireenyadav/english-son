package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockData
import com.example.data.database.ActivityItem
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: CompEnglishViewModel,
    onStartTest: (String) -> Unit,
    onSeeAllActivities: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val studentAttempts by viewModel.studentAttempts.collectAsState()
    val availableTests by viewModel.availableTests.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStudentAttempts()
    }

    // Map Supabase studentAttempts to ActivityItem for real-time rendering
    val activities = remember(studentAttempts, availableTests) {
        studentAttempts.map { attempt ->
            val correspondingTest = availableTests.firstOrNull { it.id == attempt.testId.toString() }
            ActivityItem(
                id = attempt.hashCode(), // Unique stable key for LazyColumn items
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

    // Extract user initials
    val initials = remember(userName) {
        userName.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.take(1).uppercase() }
            .ifEmpty { "JD" }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Top App Bar Header
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
                        text = "RMS Chail",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Normal,
                            letterSpacing = (-0.5).sp,
                            fontSize = 32.sp
                        ),
                        color = ThemeTextDark,
                        modifier = Modifier.testTag("app_title")
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Welcome back, ready for a test?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeTextGray
                    )
                }

                // JD User Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ThemeContainerPurple)
                        .clickable { viewModel.setTab("Profile") }
                        .testTag("profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ThemeDarkPurple
                    )
                }
            }
        }

        // 2. Recommended Featured Card (RMS Mock)
        item {
            val recommendedTest = availableTests.firstOrNull { it.id == "1" }
            if (recommendedTest != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(192.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(ThemeCardPurple)
                        .padding(20.dp)
                        .testTag("recommended_test_card")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "RECOMMENDED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = ThemeDarkPurple
                                )
                            }
                        }

                        Text(
                            text = "RMS Chail\nMock Test 1",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp,
                                lineHeight = 28.sp
                            ),
                            color = ThemeDarkPurple,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${recommendedTest.questionsCount} Questions", 
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = ThemeDarkPurple
                                )
                                Text(
                                    text = "${recommendedTest.durationMinutes} Minutes",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ThemeDarkPurple.copy(alpha = 0.7f)),
                                )
                            }

                            Button(
                                onClick = { onStartTest(recommendedTest.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ThemeDarkPurple),
                                shape = RoundedCornerShape(100.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                                modifier = Modifier.testTag("start_now_button")
                            ) {
                                Text(
                                    text = "Start Now",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. M3 Category Chips Horizontal row
        item {
            val categories = listOf("All", "Grammar", "Vocabulary", "Speaking", "Reading")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    val bgCol = if (isSelected) ThemeContainerPurple else Color.White
                    val textCol = if (isSelected) ThemeDarkPurple else ThemeTextGray
                    val borderCol = if (isSelected) ActiveChipBorder else InactiveChipBorder
                    val borderWeight = if (isSelected) 1.dp else 1.dp

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgCol)
                            .border(borderWeight, borderCol, RoundedCornerShape(12.dp))
                            .clickable { viewModel.setCategory(category) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("category_chip_$category"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = textCol
                        )
                    }
                }
            }
        }

        // 4. Recent Activity Header & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = ThemeTextGray
                )
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Purple40,
                    modifier = Modifier
                        .clickable { onSeeAllActivities() }
                        .testTag("see_all_activities")
                )
            }
        }

        // Filter and display activities dynamically
        val filteredActivities = activities.filter {
            selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
        }

        if (filteredActivities.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No activities in this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeTextGray
                    )
                }
            }
        } else {
            items(filteredActivities, key = { it.id }) { activity ->
                ActivityRowItem(activity = activity)
            }
        }
    }
}

@Composable
fun ActivityRowItem(activity: ActivityItem) {
    val dateString = remember(activity.timestamp) {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = formatter.format(Date(activity.timestamp))
        val elapsedHours = (System.currentTimeMillis() - activity.timestamp) / (1000 * 60 * 60)
        when {
            elapsedHours < 1 -> "Completed • Just now"
            elapsedHours < 24 -> "Completed • ${elapsedHours} hours ago"
            else -> "Completed • ${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(activity.timestamp))}"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity_card_${activity.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ThemeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Emoji Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ThemeIconBox),
                contentAlignment = Alignment.Center
            ) {
                Text(text = activity.iconEmoji, fontSize = 22.sp)
            }

            // Central details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = ThemeTextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                val subtitleText = if (activity.type == "In Progress") {
                    "In Progress • 80% left" // To mimic the design HTML exactly
                } else {
                    dateString
                }
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodySmall,
                    color = ThemeTextGray
                )
            }

            // Right score/indicator
            if (activity.type == "In Progress") {
                // Red active indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB3261E))
                )
            } else {
                // Percentage score
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${activity.score}%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Purple40
                    )
                    Text(
                        text = "Score",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = ThemeTextGray
                    )
                }
            }
        }
    }
}
