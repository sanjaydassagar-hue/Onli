package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        LessonProgressEntity::class,
        QuizScoreEntity::class,
        PaperTradeEntity::class,
        LiveClassEntity::class,
        SubscriptionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun quizScoreDao(): QuizScoreDao
    abstract fun paperTradeDao(): PaperTradeDao
    abstract fun liveClassDao(): LiveClassDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "share_market_bangla.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default demo investor account and live class
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.userDao()?.insertUser(
                                    UserEntity(
                                        username = "admin",
                                        passwordHash = "123456",
                                        displayName = "সঞ্জয় দাস (মেন্টর)"
                                    )
                                )
                                INSTANCE?.liveClassDao()?.insertLiveClass(
                                    LiveClassEntity(
                                        title = "লাইভ মাস্টারক্লাস: প্রাইস অ্যাকশন ও চার্ট প্যাটার্ন",
                                        topicDescription = "গুগল মিটে সরাসরি লাইভ ক্লাস। ক্যান্ডেলস্টিক ব্রেকআউট ও লাইভ মার্কেট ট্রেডিং কৌশল হাতেকলমে শেখানো হবে।",
                                        dateSchedule = "প্রতি রবিবার ও বৃহস্পতিবার, রাত ৮:৩০ টা",
                                        durationMinutes = 90,
                                        meetLink = "https://meet.google.com/ndz-jfxw-bkh",
                                        instructorName = "সঞ্জয় দাস (মেন্টর)"
                                    )
                                )
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
