package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockData
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TestTakerScreen
import com.example.ui.screens.TestsScreen
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CompEnglishViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainContent(viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: CompEnglishViewModel) {
    val loggedInStudent by viewModel.loggedInStudent.collectAsState()
    val activeTest by viewModel.activeTest.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val testLoading by viewModel.testLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (loggedInStudent == null) {
            LoginScreen(viewModel = viewModel)
        } else if (activeTest != null) {
            TestTakerScreen(
                viewModel = viewModel,
                onFinish = { viewModel.quitTest() }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    CustomBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) }
                    )
                },
                containerColor = ThemeBg
            ) { innerPadding ->
                val screenModifier = Modifier.padding(innerPadding)
                when (currentTab) {
                    "Home" -> HomeScreen(
                        viewModel = viewModel,
                        onStartTest = { testId ->
                            val test = viewModel.availableTests.value.firstOrNull { it.id == testId }
                            if (test != null) {
                                viewModel.startTest(test)
                            }
                        },
                        onSeeAllActivities = { viewModel.setTab("Tests") },
                        modifier = screenModifier
                    )
                    "Tests" -> TestsScreen(
                        viewModel = viewModel,
                        onStartTest = { testId ->
                            val test = viewModel.availableTests.value.firstOrNull { it.id == testId }
                            if (test != null) {
                                viewModel.startTest(test)
                            }
                        },
                        modifier = screenModifier
                    )
                    "Stats" -> StatsScreen(
                        viewModel = viewModel,
                        modifier = screenModifier
                    )
                    "Leaderboard" -> LeaderboardScreen(
                        viewModel = viewModel,
                        modifier = screenModifier
                    )
                    "Profile" -> ProfileScreen(
                        viewModel = viewModel,
                        modifier = screenModifier
                    )
                }
            }
        }

        if (testLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Purple40,
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "Loading test from Supabase...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = ThemeTextDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeNavBg)
            .navigationBarsPadding()
            .height(80.dp)
            .border(width = 0.5.dp, color = ThemeBorder.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            NavData("Home", Icons.Filled.Home, Icons.Outlined.Home),
            NavData("Tests", Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
            NavData("Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart),
            NavData("Leaderboard", Icons.Filled.Star, Icons.Outlined.Star),
            NavData("Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
        )

        navItems.forEach { item ->
            val isSelected = currentTab == item.name
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(item.name) }
                    .testTag("nav_tab_${item.name}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Active pill background
                val pillModifier = if (isSelected) {
                    Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(ThemeContainerPurple)
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                } else {
                    Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                }

                Box(
                    modifier = pillModifier,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                        contentDescription = item.name,
                        tint = if (isSelected) ThemeDarkPurple else ThemeTextGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = if (isSelected) ThemeDarkPurple else ThemeTextGray
                )
            }
        }
    }
}

data class NavData(
    val name: String,
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector
)

// MUST PRESERVE TO KEEP THE UNIT AND SCREENSHOT TESTS RUNNING PROPERLY
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
