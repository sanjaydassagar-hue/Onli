package com.example.data.model

data class Lesson(
    val id: String,
    val moduleId: Int,
    val moduleTitleBn: String,
    val lessonNumber: String,
    val titleBn: String,
    val summaryBn: String,
    val durationMinutes: Int,
    val sections: List<LessonSection>,
    val keyTakeawayBn: String,
    val practicalTipBn: String
)

data class LessonSection(
    val headerBn: String,
    val contentBn: String,
    val bulletPoints: List<String> = emptyList()
)

data class QuizQuestion(
    val id: Int,
    val topicBn: String,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationBn: String
)

data class GlossaryTerm(
    val id: String,
    val termEn: String,
    val termBn: String,
    val categoryBn: String,
    val definitionBn: String,
    val realLifeExampleBn: String
)

data class MockStock(
    val symbol: String,
    val nameBn: String,
    val sectorBn: String,
    val price: Double,
    val changePercent: Double,
    val peRatio: Double,
    val dividendYield: Double,
    val isBullish: Boolean
)
