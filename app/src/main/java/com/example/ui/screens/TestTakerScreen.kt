package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
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
import com.example.data.model.EnglishTest
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel

import androidx.core.text.HtmlCompat

fun String.parseHtml(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
}

@Composable
fun TestTakerScreen(
    viewModel: CompEnglishViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTest by viewModel.activeTest.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val visitedQuestions by viewModel.visitedQuestions.collectAsState()
    val answers by viewModel.answers.collectAsState()
    val testFinished by viewModel.testFinished.collectAsState()
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()
    val finalScore by viewModel.finalScore.collectAsState()

    val test = activeTest ?: return

    val totalQuestions = test.questionsCount
    val question = test.questions.getOrNull(currentIndex)

    if (testFinished) {
        // Results Summary Card screen
        TestResultSummaryView(
            viewModel = viewModel,
            test = test,
            score = finalScore,
            answers = answers,
            onClose = onFinish,
            modifier = modifier
        )
    } else {
        // Active Quiz View
        Scaffold(
            topBar = {
                OptInAppHeader(
                    title = test.title,
                    onQuit = { viewModel.quitTest() },
                    secondsRemaining = secondsRemaining
                )
            },
            modifier = modifier.fillMaxSize(),
            containerColor = ThemeBg
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Progress Bar
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of $totalQuestions",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = ThemeTextGray
                    )
                    Text(
                        text = "${((currentIndex + 1) * 100) / totalQuestions}% Completed",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Purple40
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / totalQuestions },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .testTag("quiz_progress_bar"),
                    color = Purple40,
                    trackColor = ThemeBorder.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // CBT Question Map
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(totalQuestions) { idx ->
                        val isCurrent = idx == currentIndex
                        val isAnswered = answers.containsKey(idx)
                        val isVisited = visitedQuestions.contains(idx)

                        val bgColor = when {
                            isAnswered -> Color(0xFF4CAF50) // Green
                            isVisited && !isAnswered -> Color(0xFFF44336) // Red
                            else -> ThemeBorder.copy(alpha = 0.3f) // Gray
                        }
                        
                        val textColor = when {
                            isAnswered || (isVisited && !isAnswered) -> Color.White
                            else -> ThemeTextGray
                        }
                        
                        val borderColor = if (isCurrent) ThemeDarkPurple else Color.Transparent
                        val borderWidth = if (isCurrent) 2.dp else 0.dp

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
                                .clickable { viewModel.jumpToQuestion(idx) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content area (Passage + Question + Options)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // IELTS Reading Passage
                    if (test.readingPassage != null) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reading_passage_card"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, ThemeBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Reading Passage",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = Purple40
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = test.readingPassage.parseHtml(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ThemeTextDark,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }

                    // Question prompt
                    if (question != null) {
                        item {
                            Text(
                                text = question.questionText.parseHtml(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp
                                ),
                                color = ThemeTextDark,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .testTag("question_text")
                            )
                        }

                        // Choices Cards
                        itemsIndexed(question.options) { optionIdx, optionText ->
                            val isSelected = answers[currentIndex] == optionIdx
                            val bgCol = if (isSelected) ThemeContainerPurple else Color.White
                            val borderCol = if (isSelected) Purple40 else ThemeBorder
                            val borderWeight = if (isSelected) 2.dp else 1.dp
                            val textCol = if (isSelected) ThemeDarkPurple else ThemeTextDark

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectAnswer(currentIndex, optionIdx) }
                                    .testTag("option_${currentIndex}_$optionIdx"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = bgCol),
                                border = BorderStroke(borderWeight, borderCol)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // A, B, C, D indicator pill
                                    val letter = when (optionIdx) {
                                        0 -> "A"
                                        1 -> "B"
                                        2 -> "C"
                                        else -> "D"
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Purple40 else ThemeIconBox),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) Color.White else ThemeTextGray
                                        )
                                    }

                                    Text(
                                        text = optionText.parseHtml(),
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                        color = textCol,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Sticky Bottom Navigation Buttons
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.prevQuestion() },
                        enabled = currentIndex > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("quiz_prev_button"),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ThemeDarkPurple),
                        border = BorderStroke(1.dp, ThemeBorder),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prev", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearAnswer(currentIndex) },
                        enabled = answers[currentIndex] != null,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("quiz_clear_button"),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336)),
                        border = BorderStroke(1.dp, Color(0xFFF44336).copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Clear", style = MaterialTheme.typography.labelLarge)
                    }

                    val isLast = currentIndex == totalQuestions - 1
                    val nextButtonText = if (isLast) "Finish" else "Next"
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ThemeDarkPurple),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(nextButtonText, color = Color.White, style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.width(4.dp))
                        if (isLast) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        } else {
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptInAppHeader(
    title: String,
    onQuit: () -> Unit,
    secondsRemaining: Int
) {
    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onQuit,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(ThemeIconBox)
                    .testTag("quit_test_button")
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Quit exam", tint = ThemeTextDark)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ThemeTextDark,
                maxLines = 1
            )
        }

        // Active countdown timer pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(ThemeContainerPurple)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = ThemeDarkPurple, modifier = Modifier.size(16.dp))
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = ThemeDarkPurple,
                modifier = Modifier.testTag("countdown_timer")
            )
        }
    }
}

@Composable
fun TestResultSummaryView(
    viewModel: CompEnglishViewModel,
    test: EnglishTest,
    score: Int,
    answers: Map<Int, Int>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedQuestionReview by remember { mutableStateOf<Int?>(null) }
    
    val groqExplanationLoading by viewModel.groqExplanationLoading.collectAsState()
    val groqExplanationResult by viewModel.groqExplanationResult.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
    ) {
        // Congratulatory Headers
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (score >= 70) "Excellent Try!" else "Good Effort!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    color = ThemeTextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You completed ${test.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ThemeTextGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Animated Score Circular KPI Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("score_summary_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ThemeBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var correctCount = 0
                    test.questions.forEachIndexed { idx, q ->
                        if (answers[idx] == q.correctOptionIndex) correctCount++
                    }
                    val accuracyPercentage = if (test.questionsCount > 0) (correctCount * 100) / test.questionsCount else 0

                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background sweep circle
                        CircularProgressIndicator(
                            progress = { 1.0f },
                            modifier = Modifier.fillMaxSize(),
                            color = ThemeIconBox,
                            strokeWidth = 12.dp
                        )
                        // Dynamic scored sweep
                        CircularProgressIndicator(
                            progress = { accuracyPercentage.toFloat() / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = Purple40,
                            strokeWidth = 12.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$accuracyPercentage%",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = Purple40
                            )
                            Text(
                                text = "ACCURACY",
                                style = MaterialTheme.typography.labelSmall,
                                color = ThemeTextGray
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Points", style = MaterialTheme.typography.bodySmall, color = ThemeTextGray)
                            Text("$score", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Purple40)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total", style = MaterialTheme.typography.bodySmall, color = ThemeTextGray)
                            Text("${test.questionsCount}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Correct", style = MaterialTheme.typography.bodySmall, color = ThemeTextGray)
                            Text("$correctCount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF2E7D32))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Incorrect", style = MaterialTheme.typography.bodySmall, color = ThemeTextGray)
                            var incorrectCount = 0
                            test.questions.forEachIndexed { idx, q ->
                                if (answers[idx] != null && answers[idx] != q.correctOptionIndex) incorrectCount++
                            }
                            Text("$incorrectCount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFB3261E))
                        }
                    }
                }
            }
        }

        // Question review explanations title
        item {
            Text(
                text = "QUESTION EXPLANATIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = ThemeTextGray
            )
        }

        // Detailed Review Items List
        itemsIndexed(test.questions) { index, question ->
            val userChoiceIdx = answers[index]
            val isCorrect = userChoiceIdx == question.correctOptionIndex
            val borderCol = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFB3261E)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedQuestionReview = if (selectedQuestionReview == index) null else index
                        viewModel.clearGroqExplanation()
                    }
                    .testTag("review_item_$index"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderCol.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${index + 1}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ThemeTextDark
                        )

                        Text(
                            text = if (isCorrect) "CORRECT" else "INCORRECT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = borderCol
                        )
                    }

                    Text(
                        text = question.questionText.parseHtml(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeTextDark
                    )

                    AnimatedVisibility(visible = selectedQuestionReview == index) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            HorizontalDivider(color = ThemeBorder.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(4.dp))

                            // Show the correct vs selected choices
                            Text(
                                text = "Your Answer: " + (userChoiceIdx?.let { question.options.getOrNull(it)?.parseHtml() } ?: "Not answered"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFB3261E)
                            )

                            if (!isCorrect) {
                                Text(
                                    text = "Correct Answer: " + (question.options.getOrNull(question.correctOptionIndex)?.parseHtml() ?: ""),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ThemeIconBox)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "EXPLANATION",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ThemeTextGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.explanation.parseHtml(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ThemeTextDark,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    if (groqExplanationLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally), color = Purple40, strokeWidth = 2.dp)
                                    } else if (groqExplanationResult != null) {
                                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8F5E9)).padding(12.dp)) {
                                            Column {
                                                Text("🤖 Groq AI Tutor", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF2E7D32))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(text = groqExplanationResult!!, style = MaterialTheme.typography.bodySmall, color = ThemeTextDark)
                                            }
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.askGroqToExplain(
                                                    questionText = question.questionText.parseHtml(),
                                                    correctOption = question.options.getOrNull(question.correctOptionIndex)?.parseHtml() ?: "",
                                                    userOption = userChoiceIdx?.let { question.options.getOrNull(it)?.parseHtml() }
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Purple40)
                                        ) {
                                            Text("Ask AI Tutor (Groq)", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (selectedQuestionReview != index) {
                        Text(
                            text = "Tap to show answer explanation",
                            style = MaterialTheme.typography.labelSmall,
                            color = Purple40,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        // Return Home button
        item {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = ThemeDarkPurple),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("finish_summary_button")
            ) {
                Text("Return to Dashboard", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
