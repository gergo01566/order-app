package com.example.onlab.screen.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.onlab.service.AuthService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val AuthService: AuthService
) : OrderAppViewModel() {

    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        launchCatching {
            AuthService.currentUser.collect{ user ->
                uiState.value = uiState.value.copy(
                    name = user.displayName,
                    address = user.address,
                    email = user.email,
                    image = user.image
                )
                Log.d("loggg", ": $user")
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