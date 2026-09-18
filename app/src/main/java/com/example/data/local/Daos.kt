package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash LIMIT 1")
    suspend fun authenticate(username: String, passwordHash: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface LessonProgressDao {
    @Query("SELECT * FROM lesson_progress WHERE username = :username")
    fun getProgressForUser(username: String): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE username = :username AND lessonId = :lessonId LIMIT 1")
    suspend fun getProgressForLesson(username: String, lessonId: String): LessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: LessonProgressEntity)

    @Query("UPDATE lesson_progress SET isBookmarked = :bookmarked WHERE username = :username AND lessonId = :lessonId")
    suspend fun updateBookmark(username: String, lessonId: String, bookmarked: Boolean)

    @Query("UPDATE lesson_progress SET isCompleted = :completed, completedAt = :timestamp WHERE username = :username AND lessonId = :lessonId")
    suspend fun updateCompletion(username: String, lessonId: String, completed: Boolean, timestamp: Long)

    @Query("SELECT lessonId FROM lesson_progress WHERE username = :username AND isCompleted = 1")
    fun getCompletedLessonIds(username: String): Flow<List<String>>

    @Query("SELECT lessonId FROM lesson_progress WHERE username = :username AND isBookmarked = 1")
    fun getBookmarkedLessonIds(username: String): Flow<List<String>>
}

@Dao
interface QuizScoreDao {
    @Insert
    suspend fun insertScore(score: QuizScoreEntity)

    @Query("SELECT * FROM quiz_scores WHERE username = :username ORDER BY timestamp DESC")
    fun getScoresForUser(username: String): Flow<List<QuizScoreEntity>>

    @Query("SELECT MAX(score) FROM quiz_scores WHERE username = :username")
    suspend fun getBestScore(username: String): Int?
}

@Dao
interface PaperTradeDao {
    @Query("SELECT * FROM paper_trades WHERE username = :username ORDER BY timestamp DESC")
    fun getHoldings(username: String): Flow<List<PaperTradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: PaperTradeEntity)

    @Query("DELETE FROM paper_trades WHERE id = :id")
    suspend fun deleteTrade(id: Int)

    @Query("DELETE FROM paper_trades WHERE username = :username")
    suspend fun clearPortfolio(username: String)
}

@Dao
interface LiveClassDao {
    @Query("SELECT * FROM live_classes ORDER BY id DESC")
    fun getAllLiveClasses(): Flow<List<LiveClassEntity>>

    @Query("SELECT * FROM live_classes WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveLiveClasses(): Flow<List<LiveClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveClass(liveClass: LiveClassEntity): Long

    @Query("UPDATE live_classes SET title = :title, topicDescription = :topicDescription, dateSchedule = :dateSchedule, meetLink = :meetLink WHERE id = :id")
    suspend fun updateLiveClass(id: Int, title: String, topicDescription: String, dateSchedule: String, meetLink: String)

    @Query("DELETE FROM live_classes WHERE id = :id")
    suspend fun deleteLiveClass(id: Int)

    @Query("SELECT COUNT(*) FROM live_classes")
    suspend fun getCount(): Int
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM course_subscription WHERE username = :username LIMIT 1")
    fun getSubscription(username: String): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM course_subscription WHERE username = :username LIMIT 1")
    suspend fun getSubscriptionOnce(username: String): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM course_subscription WHERE username = :username")
    suspend fun deleteSubscription(username: String)
}

