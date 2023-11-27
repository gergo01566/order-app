package com.example.onlab.screen.profile

import android.util.Log
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
    private val AuthService: AuthService,
    private val userStorageService: UserStorageService
) : OrderAppViewModel() {

    var userResponse by mutableStateOf<ValueOrException<User>>(ValueOrException.Loading)
        private set

    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        Log.d("log", "ProfileViewModel")
        launchCatching {
            userResponse = ValueOrException.Loading
                userResponse = userStorageService.getUser(AuthService.currentUserId)
                when(userResponse){
                    is ValueOrException.Success<User> -> {
                        val data = (userResponse as ValueOrException.Success<User>).data
                        Log.d("loggg", ": $data")

                        uiState.value = uiState.value.copy(
                            name = data.displayName,
                            address = data.address,
                            email = data.email,
                            image = data.image
                        )
                    }
                    else -> {
                        Log.d("loggg", ": nope")
                    }
                }

        }
    }

    fun onLogout(){
        launchCatching {
            AuthService.signOut()
        }
    }

    fun onDeleteProfile(){
        launchCatching {
            AuthService.deleteProfile()
        }
    }
}