package com.example.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: UserEntity) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AppRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser = repository.currentUser

    fun login(username: String, pass: String) {
        if (username.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("ইউজারনেম এবং পাসওয়ার্ড পূরণ করুন")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.loginUser(username, pass)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "লগইন ব্যর্থ হয়েছে")
            }
        }
    }

    fun register(username: String, pass: String, name: String) {
        if (username.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("সকল প্রয়োজনীয় তথ্য প্রদান করুন")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.registerUser(username, pass, name)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "নিবন্ধন ব্যর্থ হয়েছে")
            }
        }
    }

    fun loginDemo() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val user = repository.demoLogin()
            _uiState.value = AuthUiState.Success(user)
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
