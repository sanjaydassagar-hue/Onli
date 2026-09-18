package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.LessonProgressEntity
import com.example.data.local.PaperTradeEntity
import com.example.data.local.QuizScoreEntity
import com.example.data.local.UserEntity
import com.example.data.model.Lesson
import com.example.data.sample.BengaliCurriculumData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val lessonDao = db.lessonProgressDao()
    private val quizDao = db.quizScoreDao()
    private val paperTradeDao = db.paperTradeDao()

    private val prefs = context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    init {
        // Check for saved session
        val savedUsername = prefs.getString("logged_in_username", null)
        if (!savedUsername.isNullOrBlank()) {
            val savedDisplayName = prefs.getString("logged_in_display_name", "বিনিয়োগকারী") ?: "বিনিয়োগকারী"
            _currentUser.value = UserEntity(
                username = savedUsername,
                passwordHash = "",
                displayName = savedDisplayName
            )
        }
    }

    suspend fun registerUser(username: String, password: String, displayName: String): Result<UserEntity> {
        val cleanUsername = username.trim().lowercase()
        if (cleanUsername.length < 3) {
            return Result.failure(Exception("ইউজারনেম কমপক্ষে ৩ অক্ষরের হতে হবে"))
        }
        if (password.length < 4) {
            return Result.failure(Exception("পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে"))
        }
        val existing = userDao.getUserByUsername(cleanUsername)
        if (existing != null) {
            return Result.failure(Exception("এই ইউজারনেমটি ইতিমধ্যে ব্যবহৃত হয়েছে"))
        }

        val newUser = UserEntity(
            username = cleanUsername,
            passwordHash = password,
            displayName = displayName.ifBlank { "শেয়ার শিক্ষার্থী" }
        )
        try {
            userDao.insertUser(newUser)
            saveSession(newUser)
            return Result.success(newUser)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun loginUser(username: String, password: String): Result<UserEntity> {
        val cleanUsername = username.trim().lowercase()
        val user = userDao.authenticate(cleanUsername, password)
        return if (user != null) {
            saveSession(user)
            Result.success(user)
        } else {
            // Check if user exists but password mismatch
            val existing = userDao.getUserByUsername(cleanUsername)
            if (existing != null) {
                Result.failure(Exception("পাসওয়ার্ড সঠিক নয়"))
            } else {
                Result.failure(Exception("ইউজারনেমটি খুঁজে পাওয়া যায়নি"))
            }
        }
    }

    suspend fun demoLogin(): UserEntity {
        val demoUser = UserEntity(
            username = "investor_demo",
            passwordHash = "123456",
            displayName = "ডেল্টা ইনভেস্টর"
        )
        // Ensure inserted
        val existing = userDao.getUserByUsername(demoUser.username)
        if (existing == null) {
            userDao.insertUser(demoUser)
        }
        saveSession(demoUser)
        return demoUser
    }

    private fun saveSession(user: UserEntity) {
        _currentUser.value = user
        prefs.edit()
            .putString("logged_in_username", user.username)
            .putString("logged_in_display_name", user.displayName)
            .apply()
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().clear().apply()
    }

    fun getAllLessons(): List<Lesson> = BengaliCurriculumData.lessons

    fun getLessonById(id: String): Lesson? = BengaliCurriculumData.lessons.find { it.id == id }

    fun getCompletedLessonIds(username: String): Flow<List<String>> {
        return lessonDao.getCompletedLessonIds(username)
    }

    fun getBookmarkedLessonIds(username: String): Flow<List<String>> {
        return lessonDao.getBookmarkedLessonIds(username)
    }

    suspend fun toggleCompleted(username: String, lessonId: String, currentCompleted: Boolean) {
        val existing = lessonDao.getProgressForLesson(username, lessonId)
        if (existing == null) {
            lessonDao.upsertProgress(
                LessonProgressEntity(
                    username = username,
                    lessonId = lessonId,
                    isCompleted = !currentCompleted,
                    completedAt = if (!currentCompleted) System.currentTimeMillis() else 0L
                )
            )
        } else {
            lessonDao.updateCompletion(
                username,
                lessonId,
                !currentCompleted,
                if (!currentCompleted) System.currentTimeMillis() else 0L
            )
        }
    }

    suspend fun toggleBookmark(username: String, lessonId: String, currentBookmarked: Boolean) {
        val existing = lessonDao.getProgressForLesson(username, lessonId)
        if (existing == null) {
            lessonDao.upsertProgress(
                LessonProgressEntity(
                    username = username,
                    lessonId = lessonId,
                    isBookmarked = !currentBookmarked
                )
            )
        } else {
            lessonDao.updateBookmark(username, lessonId, !currentBookmarked)
        }
    }

    suspend fun saveQuizScore(username: String, topic: String, score: Int, total: Int) {
        quizDao.insertScore(
            QuizScoreEntity(
                username = username,
                quizTopic = topic,
                score = score,
                totalQuestions = total
            )
        )
    }

    fun getQuizScores(username: String): Flow<List<QuizScoreEntity>> {
        return quizDao.getScoresForUser(username)
    }

    suspend fun getBestQuizScore(username: String): Int {
        return quizDao.getBestScore(username) ?: 0
    }

    fun getPaperTrades(username: String): Flow<List<PaperTradeEntity>> {
        return paperTradeDao.getHoldings(username)
    }

    suspend fun executeBuyTrade(username: String, symbol: String, nameBn: String, shares: Int, price: Double) {
        paperTradeDao.insertTrade(
            PaperTradeEntity(
                username = username,
                symbol = symbol,
                companyNameBn = nameBn,
                shares = shares,
                buyPrice = price
            )
        )
    }

    suspend fun sellTrade(id: Int) {
        paperTradeDao.deleteTrade(id)
    }

    suspend fun resetPaperTrading(username: String) {
        paperTradeDao.clearPortfolio(username)
    }

    // Live Class & Course Subscription features
    private val defaultUpiId = "8967254968@ybl"
    private val keyCourseFee = "custom_course_fee_amount"
    private val keyUpiId = "custom_upi_id"

    fun getCourseFee(): Int {
        return prefs.getInt(keyCourseFee, 1999)
    }

    fun setCourseFee(fee: Int) {
        prefs.edit().putInt(keyCourseFee, fee).apply()
    }

    fun getUpiId(): String {
        return prefs.getString(keyUpiId, defaultUpiId) ?: defaultUpiId
    }

    fun setUpiId(upi: String) {
        prefs.edit().putString(keyUpiId, upi.trim()).apply()
    }

    fun getLiveClasses(): Flow<List<com.example.data.local.LiveClassEntity>> {
        return db.liveClassDao().getAllLiveClasses()
    }

    suspend fun addLiveClass(title: String, topic: String, schedule: String, meetLink: String, instructor: String = "সঞ্জয় দাস") {
        db.liveClassDao().insertLiveClass(
            com.example.data.local.LiveClassEntity(
                title = title.trim(),
                topicDescription = topic.trim(),
                dateSchedule = schedule.trim(),
                meetLink = meetLink.trim(),
                instructorName = instructor.trim()
            )
        )
    }

    suspend fun updateLiveClass(id: Int, title: String, topic: String, schedule: String, meetLink: String) {
        db.liveClassDao().updateLiveClass(id, title.trim(), topic.trim(), schedule.trim(), meetLink.trim())
    }

    suspend fun deleteLiveClass(id: Int) {
        db.liveClassDao().deleteLiveClass(id)
    }

    fun getUserSubscription(username: String): Flow<com.example.data.local.SubscriptionEntity?> {
        return db.subscriptionDao().getSubscription(username)
    }

    suspend fun recordPaymentSubscription(username: String, transactionRef: String, feeAmount: Int): Result<Unit> {
        if (transactionRef.trim().length < 4) {
            return Result.failure(Exception("সঠিক লেনদেন রেফারেন্স বা UTR নম্বর লিখুন"))
        }
        val sub = com.example.data.local.SubscriptionEntity(
            username = username,
            courseFee = feeAmount,
            planDurationMonths = 3,
            paymentUpiId = getUpiId(),
            transactionRef = transactionRef.trim(),
            isApproved = true,
            paymentTimestamp = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000)
        )
        db.subscriptionDao().insertSubscription(sub)
        return Result.success(Unit)
    }

    suspend fun revokeSubscription(username: String) {
        db.subscriptionDao().deleteSubscription(username)
    }
}

