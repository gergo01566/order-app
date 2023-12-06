package com.example.onlab.screen.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.User
import com.example.onlab.service.AuthService
import com.example.onlab.service.UserStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AuthService,
    private val userStorageService: UserStorageService
) : OrderAppViewModel() {

    var userResponse by mutableStateOf<ValueOrException<User>>(ValueOrException.Loading)
        private set

    var logOutResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var deleteProfileResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        launchCatching {
            userResponse = ValueOrException.Loading
                userResponse = userStorageService.getUser(authService.currentUserId)
                when(userResponse){
                    is ValueOrException.Success<User> -> {
                        val data = (userResponse as ValueOrException.Success<User>).data

                        uiState.value = uiState.value.copy(
                            name = data.displayName,
                            address = data.address,
                            email = data.email,
                            image = data.image
                        )
                    }
                    else -> {
                    }
                }

        }
    }

    fun onLogout(){
        launchCatching {
            logOutResponse = ValueOrException.Loading
            logOutResponse = authService.signOut()
            when(val response = logOutResponse){
                is ValueOrException.Success -> Unit
                else -> Unit
            }
        }
    }

    fun onDeleteProfile(){
        launchCatching {
            deleteProfileResponse = ValueOrException.Loading
            deleteProfileResponse = authService.deleteProfile()
            when(val response = deleteProfileResponse){
                is ValueOrException.Success -> Unit
                else -> Unit
            }

        }
    }
}