package com.example.onlab.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.onlab.R
import com.example.onlab.components.SnackbarManager
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.User
import com.example.onlab.navigation.DestinationCustomerList
import com.example.onlab.navigation.DestinationLogin
import com.example.onlab.navigation.DestinationOrderList
import com.example.onlab.screen.customer.ValidationUtils
import com.example.onlab.screen.login.LoginUiState
import com.example.onlab.service.AuthService
import com.example.onlab.service.UserStorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.sign
import com.example.onlab.R.string as AppText


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
): OrderAppViewModel() {
    var signInResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var signUpResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var resetPasswordResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

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
            SnackbarManager.displayMessage(R.string.invalid_email_error)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.displayMessage(R.string.invalid_email_error)
            return
        }

        launchCatching  {
            signInResponse = ValueOrException.Loading

            try {
                signInResponse = authService.signInWithEmailAndPassowrd(email, password)

                when (val response = signInResponse) {
                    is ValueOrException.Success -> {
                        if (response.data) {
                            withContext(Dispatchers.Main) {
                                navigateFromTo(DestinationLogin, DestinationCustomerList)
                            }
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            SnackbarManager.displayMessage(R.string.sign_in_error)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    SnackbarManager.displayMessage(R.string.sign_in_error)
                }
            }
        }
    }

    fun onSignUpClick(navigateFromTo: (String, String) -> Unit) {
        if (!ValidationUtils.inputIsValidEmailFormat(email) || !ValidationUtils.inputIsValidPassword(password)){
            SnackbarManager.displayMessage(R.string.invalid_password)
            return
        }

        launchCatching {
            signUpResponse = ValueOrException.Loading

            try {
                signUpResponse = authService.createUser(email, password)

                when (val response = signUpResponse) {
                    is ValueOrException.Success -> {
                        if (response.data) {
                            withContext(Dispatchers.Main) {
                                navigateFromTo(DestinationLogin, DestinationCustomerList)
                            }
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            SnackbarManager.displayMessage(R.string.sign_up_error)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    SnackbarManager.displayMessage(R.string.sign_up_error)
                }
            }
        }
    }

    fun onResetPassword(email: String){
        if (email.isBlank()) {
            return
        }
        launchCatching {
            resetPasswordResponse = ValueOrException.Loading

            try {
                resetPasswordResponse = authService.resetPassword(email)

                when (val response = resetPasswordResponse) {
                    is ValueOrException.Success -> {
                        if (response.data) {
                            withContext(Dispatchers.Main) {
                                SnackbarManager.displayMessage(R.string.email_sent_to_recover_pwd)
                            }
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            SnackbarManager.displayMessage(R.string.invalid_email_error)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    SnackbarManager.displayMessage(R.string.invalid_email_error)
                }
            }
        }
    }


}