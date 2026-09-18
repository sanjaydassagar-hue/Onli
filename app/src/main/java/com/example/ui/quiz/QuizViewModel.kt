package com.example.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.QuizQuestion
import com.example.data.repository.AppRepository
import com.example.data.sample.BengaliQuizData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val score: Int = 0,
    val isQuizFinished: Boolean = false,
    val bestScore: Int = 0
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    val currentUser = repository.currentUser

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        startNewQuiz()
    }

    fun startNewQuiz() {
        val user = currentUser.value?.username ?: ""
        viewModelScope.launch {
            val best = if (user.isNotEmpty()) repository.getBestQuizScore(user) else 0
            val shuffled = BengaliQuizData.questions.shuffled()
            _uiState.value = QuizUiState(
                questions = shuffled,
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                isAnswerSubmitted = false,
                score = 0,
                isQuizFinished = false,
                bestScore = best
            )
        }
    }

    fun selectOption(index: Int) {
        if (_uiState.value.isAnswerSubmitted) return
        _uiState.value = _uiState.value.copy(selectedOptionIndex = index)
    }

    fun submitAnswer() {
        val state = _uiState.value
        val selected = state.selectedOptionIndex ?: return
        val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return

        val isCorrect = selected == currentQ.correctIndex
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.value = state.copy(
            isAnswerSubmitted = true,
            score = newScore
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1
        if (nextIndex < state.questions.size) {
            _uiState.value = state.copy(
                currentQuestionIndex = nextIndex,
                selectedOptionIndex = null,
                isAnswerSubmitted = false
            )
        } else {
            // Finish Quiz
            _uiState.value = state.copy(isQuizFinished = true)
            // Save to room
            val user = currentUser.value?.username ?: "investor"
            viewModelScope.launch {
                repository.saveQuizScore(
                    username = user,
                    topic = "সাধারণ শেয়ার বাজার টেস্ট",
                    score = state.score,
                    total = state.questions.size
                )
            }
        }
    }
}
