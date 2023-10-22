package com.example.onlab.viewModels

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.example.onlab.screen.login.LoginUiState
import com.example.onlab.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
): OrderAppViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(onFailure: () -> Unit, onComplete: () -> Unit ) {
        if (email.isBlank()) {
            return
        }

        if (password.isBlank()) {
            return
        }

        launchCatching {
            authService.signInWithEmailAndPassowrd(email, password, onFailure, onComplete)
        }
    }

    fun onSignUpClick( onFailure: () -> Unit, onComplete: () -> Unit) {
        if (email.isBlank()) {
            return
        }

        if (password.isBlank()) {
            return
        }

        launchCatching {
            authService.createUser(email, password, onFailure, onComplete)
        }
    }

    fun onResetPassword(email: String, onFailure: () -> Unit, onComplete: () -> Unit){
        if (email.isBlank()) {
            return
        }

        launchCatching {
            authService.resetPassword(email, onFailure, onComplete)
        }
    }


}