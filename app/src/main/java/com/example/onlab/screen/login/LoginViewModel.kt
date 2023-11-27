package com.example.onlab.viewModels

import androidx.compose.runtime.mutableStateOf
import com.example.onlab.R
import com.example.onlab.components.SnackbarManager
import com.example.onlab.model.User
import com.example.onlab.navigation.DestinationLogin
import com.example.onlab.navigation.DestinationOrderList
import com.example.onlab.screen.login.LoginUiState
import com.example.onlab.service.AuthService
import com.example.onlab.service.UserStorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.onlab.R.string as AppText


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val userStorageService: UserStorageService
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

    fun onSignInClick(navigateFromTo: (String, String) -> Unit, onFailure: () -> Unit, onComplete: () -> Unit ) {
        if (email.isBlank()) {
            return
        }

        if (password.isBlank()) {
            return
        }

        launchCatching {
            authService.signInWithEmailAndPassowrd(email, password, onFailure) {
                navigateFromTo(DestinationLogin, DestinationOrderList)
            }
        }
    }
    fun onSignUpClick(navigateFromTo: (String, String) -> Unit, onFailure: () -> Unit, onComplete: () -> Unit) {
        var user = User()
        if (email.isBlank()) {
            return
        }

        if (password.isBlank()) {
            return
        }

        launchCatching {
            authService.createUser(email, password, onFailure) {
                user = it
                navigateFromTo(DestinationLogin, DestinationOrderList)
            }
            userStorageService.addUser(user)
        }
    }

    fun onResetPassword(email: String, onFailure: () -> Unit, onComplete: () -> Unit){
        if (email.isBlank()) {
            return
        }

        launchCatching {
            authService.resetPassword(
                email = email,
                onFailure = {
                    SnackbarManager.displayMessage(AppText.invalid_email_error)
                    onFailure()
                },
                onComplete = {
                    SnackbarManager.displayMessage(AppText.email_sent_to_recover_pwd)
                    onComplete()
                }
            )
        }
    }


}