package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val displayName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "lesson_progress",
    primaryKeys = ["username", "lessonId"]
)
data class LessonProgressEntity(
    val username: String,
    val lessonId: String,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val completedAt: Long = 0L
)

@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val quizTopic: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "paper_trades")
data class PaperTradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val symbol: String,
    val companyNameBn: String,
    val shares: Int,
    val buyPrice: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "live_classes")
data class LiveClassEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val topicDescription: String,
    val dateSchedule: String,       // e.g. "রবিবার ও বুধবার, রাত ৯:০০ টা"
    val durationMinutes: Int = 90,
    val meetLink: String,           // Google Meet Link, e.g. "https://meet.google.com/abc-defg-hij"
    val instructorName: String = "সঞ্জয় দাস",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_subscription")
data class SubscriptionEntity(
    @PrimaryKey
    val username: String,
    val courseFee: Int,              // Amount paid, default 1999
    val planDurationMonths: Int = 3,
    val paymentUpiId: String,        // 8967254968@ybl
    val transactionRef: String,      // UPI UTR / Transaction ID entered by student
    val isApproved: Boolean = true,  // Granted access
    val paymentTimestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000) // 90 days validity
)

