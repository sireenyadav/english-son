package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "student_code") val studentCode: String,
    @Json(name = "last_login") val lastLogin: String? = null,
    @Json(name = "device_token") val deviceToken: String
)

@JsonClass(generateAdapter = true)
data class LeaderboardEntry(
    @Json(name = "student_code") val studentCode: String,
    @Json(name = "tests_taken") val testsTaken: Int,
    @Json(name = "avg_accuracy") val avgAccuracy: Int,
    @Json(name = "avg_time_taken") val avgTimeTaken: Int,
    @Json(name = "best_score") val bestScore: Float
)

@JsonClass(generateAdapter = true)
data class Question(
    @Json(name = "Question_ID") val questionId: Int,
    @Json(name = "Question") val question: String,
    @Json(name = "Option_0") val option0: String,
    @Json(name = "Option_1") val option1: String,
    @Json(name = "Option_2") val option2: String,
    @Json(name = "Option_3") val option3: String,
    @Json(name = "Correct_Answer_Index") val correctAnswerIndex: String,
    @Json(name = "Solution") val solution: String? = null
)

@JsonClass(generateAdapter = true)
data class TestAttempt(
    @Json(name = "student_code") val studentCode: String,
    @Json(name = "test_id") val testId: Int,
    val score: Int,
    @Json(name = "correct_questions") val correctQuestions: Int,
    val accuracy: Int,
    @Json(name = "time_taken") val timeTaken: Int,
    @Json(name = "completed_at") val completedAt: String? = null
)

interface SupabaseApi {
    @GET("rest/v1/users")
    suspend fun getUserByCode(
        @Query("student_code") studentCodeEq: String, // Pass "eq.$code"
        @Query("select") select: String = "*"
    ): List<User>

    @POST("rest/v1/users")
    suspend fun createUser(
        @Body user: User
    )

    @PATCH("rest/v1/users")
    suspend fun updateUser(
        @Query("student_code") studentCodeEq: String, // Pass "eq.$code"
        @Body user: User
    )

    @GET("rest/v1/leaderboard")
    suspend fun getLeaderboard(
        @Query("select") select: String = "*",
        @Query("order") order: String = "avg_accuracy.desc,avg_time_taken.asc",
        @Query("limit") limit: Int = 15
    ): List<LeaderboardEntry>

    @GET("rest/v1/questions")
    suspend fun getQuestions(
        @Query("select") select: String = "*",
        @Query("order") order: String = "Question_ID.asc",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20
    ): List<Question>

    @POST("rest/v1/test_attempts")
    suspend fun insertTestAttempt(
        @Body attempt: TestAttempt
    )

    @GET("rest/v1/test_attempts")
    suspend fun getTestAttempts(
        @Query("student_code") studentCodeEq: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "completed_at.desc"
    ): List<TestAttempt>
}

