package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockData
import com.example.data.database.ActivityItem
import com.example.data.database.AppDatabase
import com.example.data.model.EnglishTest
import com.example.data.repository.ActivityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

import com.example.data.network.NetworkModule
import com.example.data.network.User
import com.example.data.network.LeaderboardEntry
import com.example.data.network.TestAttempt

class CompEnglishViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = ActivityRepository(db.activityDao())

    // Student Login State
    private val _loggedInStudent = MutableStateFlow<User?>(null)
    val loggedInStudent: StateFlow<User?> = _loggedInStudent.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Leaderboard State
    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _availableTests = MutableStateFlow<List<EnglishTest>>(emptyList())
    val availableTests: StateFlow<List<EnglishTest>> = _availableTests.asStateFlow()

    private val _studentAttempts = MutableStateFlow<List<TestAttempt>>(emptyList())
    val studentAttempts: StateFlow<List<TestAttempt>> = _studentAttempts.asStateFlow()

    private val _testLoading = MutableStateFlow(false)
    val testLoading: StateFlow<Boolean> = _testLoading.asStateFlow()

    // All persisted activities
    val activities: StateFlow<List<ActivityItem>> = repository.allActivities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Bottom Navigation State
    private val _currentTab = MutableStateFlow("Home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Chip Category Filter
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Active Test Taker Session
    private val _activeTest = MutableStateFlow<EnglishTest?>(null)
    val activeTest: StateFlow<EnglishTest?> = _activeTest.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // Map of question index -> selected option index
    private val _answers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val answers: StateFlow<Map<Int, Int>> = _answers.asStateFlow()

    private val _testFinished = MutableStateFlow(false)
    val testFinished: StateFlow<Boolean> = _testFinished.asStateFlow()

    private val _finalScore = MutableStateFlow(0) // Points
    val finalScore: StateFlow<Int> = _finalScore.asStateFlow()

    // Countdown timer for active test
    private val _secondsRemaining = MutableStateFlow(0)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    // User Profile settings
    private val _userName = MutableStateFlow("Student")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userTargetScore = MutableStateFlow("8.0")
    val userTargetScore: StateFlow<String> = _userTargetScore.asStateFlow()

    private val _userDailyGoalMin = MutableStateFlow(60)
    val userDailyGoalMin: StateFlow<Int> = _userDailyGoalMin.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadRealTests()
        // Pre-populate with default items from the mockup if the database is empty
        viewModelScope.launch {
            repository.allActivities.collect { list ->
                if (list.isEmpty()) {
                    // Pre-populate:
                    repository.insertActivity(
                        ActivityItem(
                            title = "Tense & Aspect Quiz",
                            type = "Completed",
                            category = "Grammar",
                            score = 85,
                            totalQuestions = 5,
                            timeSpentSec = 180,
                            timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                            iconEmoji = "📝"
                        )
                    )
                }
            }
        }
    }

    private fun loadRealTests() {
        val tests = listOf(
            EnglishTest(
                id = "1",
                title = "IELTS Reading Mock 1",
                category = "Reading",
                durationMinutes = 60,
                questionsCount = 20,
                iconEmoji = "📖",
                description = "IELTS Academic Reading mock test. Real-time questions fetched from Supabase.",
                questions = emptyList()
            ),
            EnglishTest(
                id = "2",
                title = "Grammar & Structure Quiz",
                category = "Grammar",
                durationMinutes = 60,
                questionsCount = 20,
                iconEmoji = "✍️",
                description = "Grammar checking and error recognition. Real-time questions fetched from Supabase.",
                questions = emptyList()
            ),
            EnglishTest(
                id = "3",
                title = "Vocabulary Masterclass",
                category = "Vocabulary",
                durationMinutes = 60,
                questionsCount = 20,
                iconEmoji = "🎯",
                description = "Synonyms, antonyms, and blank-filling exercises. Real-time questions fetched from Supabase.",
                questions = emptyList()
            ),
            EnglishTest(
                id = "4",
                title = "Speaking Sentence Flow",
                category = "Speaking",
                durationMinutes = 60,
                questionsCount = 20,
                iconEmoji = "🗣️",
                description = "Conversational flow, phrases, and structures. Real-time questions fetched from Supabase.",
                questions = emptyList()
            ),
            EnglishTest(
                id = "5",
                title = "Advanced Reading Comprehension",
                category = "Reading",
                durationMinutes = 60,
                questionsCount = 20,
                iconEmoji = "📝",
                description = "Advanced reading challenge with high complexity stems. Real-time questions fetched from Supabase.",
                questions = emptyList()
            )
        )
        _availableTests.value = tests
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun updateProfile(name: String, targetScore: String, dailyGoal: Int) {
        _userName.value = name
        _userTargetScore.value = targetScore
        _userDailyGoalMin.value = dailyGoal
    }

    fun startTest(test: EnglishTest) {
        viewModelScope.launch {
            _testLoading.value = true
            try {
                val testIdInt = test.id.toIntOrNull() ?: 1
                val offset = (testIdInt - 1) * 20
                var dbQuestions = emptyList<com.example.data.network.Question>()
                
                try {
                    dbQuestions = NetworkModule.supabaseApi.getQuestions(offset = offset, limit = 20)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (dbQuestions.isEmpty()) {
                    try {
                        dbQuestions = NetworkModule.supabaseApi.getQuestions(offset = 0, limit = 20)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (dbQuestions.isNotEmpty()) {
                    val mappedQuestions = dbQuestions.map { q ->
                        com.example.data.model.Question(
                            id = q.questionId,
                            questionText = q.question,
                            options = listOf(q.option0, q.option1, q.option2, q.option3),
                            correctOptionIndex = q.correctAnswerIndex.toIntOrNull() ?: 0,
                            explanation = q.solution ?: "No explanation provided"
                        )
                    }
                    val populatedTest = test.copy(
                        questions = mappedQuestions,
                        questionsCount = mappedQuestions.size
                    )
                    _activeTest.value = populatedTest
                    _currentQuestionIndex.value = 0
                    _visitedQuestions.value = setOf(0)
                    _answers.value = emptyMap()
                    _testFinished.value = false
                    _finalScore.value = 0
                    _secondsRemaining.value = populatedTest.durationMinutes * 60

                    timerJob?.cancel()
                    timerJob = launch {
                        val inProgressItem = ActivityItem(
                            title = populatedTest.title,
                            type = "In Progress",
                            category = populatedTest.category,
                            score = 0,
                            totalQuestions = populatedTest.questionsCount,
                            timeSpentSec = 0,
                            timestamp = System.currentTimeMillis(),
                            iconEmoji = populatedTest.iconEmoji
                        )
                        repository.insertActivity(inProgressItem)

                        while (_secondsRemaining.value > 0 && !_testFinished.value) {
                            delay(1000)
                            _secondsRemaining.value -= 1
                        }
                        if (_secondsRemaining.value == 0 && !_testFinished.value) {
                            finishTest()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _testLoading.value = false
            }
        }
    }

    private val _visitedQuestions = MutableStateFlow<Set<Int>>(setOf(0))
    val visitedQuestions: StateFlow<Set<Int>> = _visitedQuestions.asStateFlow()

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        val currentAnswers = _answers.value.toMutableMap()
        currentAnswers[questionIndex] = optionIndex
        _answers.value = currentAnswers
    }

    fun clearAnswer(questionIndex: Int) {
        val currentAnswers = _answers.value.toMutableMap()
        currentAnswers.remove(questionIndex)
        _answers.value = currentAnswers
    }

    fun jumpToQuestion(index: Int) {
        val totalQuestions = _activeTest.value?.questionsCount ?: 0
        if (index in 0 until totalQuestions) {
            _currentQuestionIndex.value = index
            val currentVisited = _visitedQuestions.value.toMutableSet()
            currentVisited.add(index)
            _visitedQuestions.value = currentVisited
        }
    }

    fun nextQuestion() {
        val currentIdx = _currentQuestionIndex.value
        val totalQuestions = _activeTest.value?.questionsCount ?: 0
        if (currentIdx < totalQuestions - 1) {
            _currentQuestionIndex.value = currentIdx + 1
            val currentVisited = _visitedQuestions.value.toMutableSet()
            currentVisited.add(currentIdx + 1)
            _visitedQuestions.value = currentVisited
        } else {
            finishTest()
        }
    }

    fun prevQuestion() {
        val currentIdx = _currentQuestionIndex.value
        if (currentIdx > 0) {
            _currentQuestionIndex.value = currentIdx - 1
            val currentVisited = _visitedQuestions.value.toMutableSet()
            currentVisited.add(currentIdx - 1)
            _visitedQuestions.value = currentVisited
        }
    }

    fun finishTest() {
        timerJob?.cancel()
        val test = _activeTest.value ?: return
        val currentAnswers = _answers.value
        var correctCount = 0
        var incorrectCount = 0
        var skippedCount = 0

        test.questions.forEachIndexed { index, question ->
            val answer = currentAnswers[index]
            if (answer == null) {
                skippedCount++
            } else if (answer == question.correctOptionIndex) {
                correctCount++
            } else {
                incorrectCount++
            }
        }

        // Under negative marking: +4 for correct, -1.3 for incorrect
        val rawScore = (correctCount * 4.0) - (incorrectCount * 1.3)
        val finalScoreVal = rawScore.roundToInt()

        val accuracy = if (test.questionsCount > 0) {
            (correctCount * 100) / test.questionsCount
        } else {
            0
        }

        val timeTaken = (test.durationMinutes * 60) - _secondsRemaining.value

        _finalScore.value = finalScoreVal
        _testFinished.value = true

        viewModelScope.launch {
            val completedItem = ActivityItem(
                title = test.title,
                type = "Completed",
                category = test.category,
                score = accuracy, // local uses accuracy for ui
                totalQuestions = test.questionsCount,
                timeSpentSec = timeTaken,
                timestamp = System.currentTimeMillis(),
                iconEmoji = test.iconEmoji
            )
            repository.insertActivity(completedItem)

            val studentCode = _loggedInStudent.value?.studentCode
            if (studentCode != null) {
                try {
                    val attempt = TestAttempt(
                        studentCode = studentCode,
                        testId = test.id.toIntOrNull() ?: 1,
                        score = finalScoreVal,
                        correctQuestions = correctCount,
                        accuracy = accuracy,
                        timeTaken = timeTaken,
                        completedAt = java.time.Instant.now().toString()
                    )
                    NetworkModule.supabaseApi.insertTestAttempt(attempt)
                    fetchLeaderboard() // refresh leaderboard after finish
                    fetchStudentAttempts() // refresh student attempts after finish
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun fetchStudentAttempts() {
        val code = _loggedInStudent.value?.studentCode ?: return
        viewModelScope.launch {
            try {
                val attempts = NetworkModule.supabaseApi.getTestAttempts("eq.$code")
                _studentAttempts.value = attempts
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _groqExplanationLoading = MutableStateFlow(false)
    val groqExplanationLoading: StateFlow<Boolean> = _groqExplanationLoading.asStateFlow()

    private val _groqExplanationResult = MutableStateFlow<String?>(null)
    val groqExplanationResult: StateFlow<String?> = _groqExplanationResult.asStateFlow()

    fun askGroqToExplain(questionText: String, correctOption: String, userOption: String?) {
        _groqExplanationLoading.value = true
        _groqExplanationResult.value = null
        viewModelScope.launch {
            try {
                val prompt = "Please explain the answer to the following question:\n\nQuestion: $questionText\n\nCorrect Answer: $correctOption\n" +
                        if (userOption != null) "\nUser's Answer: $userOption\n" else "" +
                        "\nExplain why the correct answer is right and why the user's answer (if different) might be wrong. Keep it concise."
                        
                val request = com.example.data.network.GroqChatRequest(
                    model = "llama3-8b-8192", // Fast OSS model
                    messages = listOf(
                        com.example.data.network.GroqMessage(role = "system", content = "You are a helpful and concise AI tutor for students of Rashtriya Military School Chail."),
                        com.example.data.network.GroqMessage(role = "user", content = prompt)
                    )
                )
                val response = NetworkModule.groqApi.createChatCompletion(request)
                _groqExplanationResult.value = response.choices.firstOrNull()?.message?.content ?: "Could not get an explanation."
            } catch (e: Exception) {
                _groqExplanationResult.value = "Error fetching explanation: ${e.message}"
            } finally {
                _groqExplanationLoading.value = false
            }
        }
    }

    fun clearGroqExplanation() {
        _groqExplanationResult.value = null
    }

    fun quitTest() {
        timerJob?.cancel()
        _activeTest.value = null
        _testFinished.value = false
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun loginWithCode(code: String) {
        viewModelScope.launch {
            try {
                _loginError.value = null
                val users = NetworkModule.supabaseApi.getUserByCode("eq.$code")
                if (users.isNotEmpty()) {
                    val existingUser = users.first()
                    val updatedUser = existingUser.copy(
                        lastLogin = java.time.Instant.now().toString(),
                        deviceToken = "android-app"
                    )
                    NetworkModule.supabaseApi.updateUser("eq.$code", updatedUser)
                    _loggedInStudent.value = updatedUser
                    _userName.value = "Student $code"
                    fetchStudentAttempts()
                } else {
                    val newUser = User(
                        studentCode = code,
                        lastLogin = java.time.Instant.now().toString(),
                        deviceToken = "android-app"
                    )
                    NetworkModule.supabaseApi.createUser(newUser)
                    _loggedInStudent.value = newUser
                    _userName.value = "Student $code"
                    fetchStudentAttempts()
                }
            } catch (e: Exception) {
                _loginError.value = "Failed to login: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun fetchLeaderboard() {
        viewModelScope.launch {
            try {
                val entries = NetworkModule.supabaseApi.getLeaderboard()
                _leaderboard.value = entries
            } catch (e: Exception) {
                // Ignore or handle silently for now
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        _loggedInStudent.value = null
        _currentTab.value = "Home"
    }
}
