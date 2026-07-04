package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.data.MockData
import com.example.data.model.EnglishTest
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel

@Composable
fun TestsScreen(
    viewModel: CompEnglishViewModel,
    onStartTest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = listOf("All", "Grammar", "Vocabulary", "Speaking", "Reading")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        // Title Header
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Practice Tests",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp
            ),
            color = ThemeTextDark,
            modifier = Modifier.testTag("tests_title")
        )
        Text(
            text = "Select a custom module to improve your skills",
            style = MaterialTheme.typography.bodyMedium,
            color = ThemeTextGray
        )
        Spacer(modifier = Modifier.height(20.dp))

        // In-Screen Category Tabs
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                val index = categories.indexOf(selectedCategory).coerceAtLeast(0)
                if (index < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = Purple40
                    )
                }
            }
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.setCategory(category) },
                    modifier = Modifier.testTag("tab_$category"),
                    text = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Purple40 else ThemeTextGray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tests List
        val availableTests by viewModel.availableTests.collectAsState()
        val filteredTests = remember(selectedCategory, availableTests) {
            availableTests.filter {
                selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredTests, key = { it.id }) { test ->
                TestCardItem(test = test, onStart = onStartTest)
            }
        }
    }
}

@Composable
fun TestCardItem(test: EnglishTest, onStart: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("test_card_${test.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ThemeBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Large emoji icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ThemeIconBox),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = test.iconEmoji, fontSize = 24.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(ThemeContainerPurple)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = test.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = ThemeDarkPurple
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = test.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ThemeTextDark
                    )
                }
            }

            Text(
                text = test.description,
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeTextGray,
                lineHeight = 20.sp
            )

            HorizontalDivider(color = ThemeBorder.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(
                            text = "Questions",
                            style = MaterialTheme.typography.labelSmall,
                            color = ThemeTextGray
                        )
                        Text(
                            text = "${test.questionsCount} Items",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ThemeTextDark
                        )
                    }
                    Column {
                        Text(
                            text = "Time Limit",
                            style = MaterialTheme.typography.labelSmall,
                            color = ThemeTextGray
                        )
                        Text(
                            text = "${test.durationMinutes} mins",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ThemeTextDark
                        )
                    }
                }

                Button(
                    onClick = { onStart(test.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = ThemeDarkPurple),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.testTag("start_test_${test.id}")
                ) {
                    Text(
                        text = "Take Test",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}
