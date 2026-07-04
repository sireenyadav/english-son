package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel

@Composable
fun ProfileScreen(
    viewModel: CompEnglishViewModel,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val userTargetScore by viewModel.userTargetScore.collectAsState()
    val userDailyGoalMin by viewModel.userDailyGoalMin.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    var nameInput by remember { mutableStateOf(userName) }
    var targetScoreInput by remember { mutableStateOf(userTargetScore) }
    var dailyGoalInput by remember { mutableStateOf(userDailyGoalMin.toString()) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Initials extraction
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
        // Title Header
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Your Profile",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.sp
                    ),
                    color = ThemeTextDark,
                    modifier = Modifier.testTag("profile_title")
                )
                Text(
                    text = "Manage your exam study goals & target preferences",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ThemeTextGray
                )
            }
        }

        // Avatar Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_avatar_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ThemeBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ThemeContainerPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = ThemeDarkPurple
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = ThemeTextDark
                        )
                        Text(
                            text = "English Test Aspirant",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ThemeTextGray
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            nameInput = userName
                            targetScoreInput = userTargetScore
                            dailyGoalInput = userDailyGoalMin.toString()
                            showEditDialog = true
                        },
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Purple40),
                        border = BorderStroke(1.dp, Purple40),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Text("Edit", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Target settings list card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("goals_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ThemeBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "STUDY FOCUS & TARGETS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ThemeTextGray
                    )

                    GoalSettingRow(
                        icon = Icons.Default.Adjust,
                        label = "IELTS Academic Target",
                        value = "Band $userTargetScore",
                        color = Purple40
                    )

                    HorizontalDivider(color = ThemeBorder.copy(alpha = 0.5f))

                    GoalSettingRow(
                        icon = Icons.Default.Timer,
                        label = "Daily Study Target",
                        value = "$userDailyGoalMin Minutes",
                        color = ThemeDarkPurple
                    )

                    HorizontalDivider(color = ThemeBorder.copy(alpha = 0.5f))

                    GoalSettingRow(
                        icon = Icons.Default.WorkspacePremium,
                        label = "Prepped Rank",
                        value = "Platinum Scholar",
                        color = Color(0xFFD0BCFF)
                    )
                }
            }
        }

        // Preferences card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preferences_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ThemeBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "APP PREFERENCES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ThemeTextGray
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = ThemeTextGray)
                            Text("Dark Theme Simulation", style = MaterialTheme.typography.bodyLarge, color = ThemeTextDark)
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { viewModel.toggleTheme() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Purple40),
                            modifier = Modifier.testTag("theme_switch")
                        )
                    }

                    HorizontalDivider(color = ThemeBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ThemeTextGray)
                            Text("Audio Pronunciation Guides", style = MaterialTheme.typography.bodyLarge, color = ThemeTextDark)
                        }
                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(checkedThumbColor = Purple40)
                        )
                    }
                }
            }
        }

        // Logout Button
        item {
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("logout_button"),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
            ) {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
            }
        }
    }

    // Edit Profile Modal Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Study Targets") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_text_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetScoreInput,
                        onValueChange = { targetScoreInput = it },
                        label = { Text("IELTS Target Score (e.g., 8.0)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("target_score_text_field"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dailyGoalInput,
                        onValueChange = { dailyGoalInput = it },
                        label = { Text("Daily Study Time (mins)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_goal_text_field"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = dailyGoalInput.toIntOrNull() ?: userDailyGoalMin
                        viewModel.updateProfile(nameInput, targetScoreInput, mins)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ThemeDarkPurple),
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = ThemeTextGray)
                }
            }
        )
    }
}

@Composable
fun GoalSettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = ThemeTextGray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = ThemeTextDark)
        }
    }
}
