package com.example.onlab.screen.profile

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
                    name = user.address.split("@")?.get(0) as String,
                    email = user.address
                )
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