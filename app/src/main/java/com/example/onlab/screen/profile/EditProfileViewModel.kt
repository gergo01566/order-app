package com.example.onlab.screen.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.User
import com.example.onlab.navigation.DestinationEditProfile
import com.example.onlab.navigation.DestinationProfile
import com.example.onlab.service.AuthService
import com.example.onlab.service.UserStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authService: AuthService,
    private val userStorageService: UserStorageService
): OrderAppViewModel(){
    var updateUserResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        launchCatching {
            authService.currentUser.collect { user ->
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

    fun onNameChange(newValue: String){
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onAddressChange(newValue: String){
        uiState.value = uiState.value.copy(address = newValue)
    }

    fun onImageChange(newValue: String){
        uiState.value = uiState.value.copy(image = newValue)
    }

    fun onUpdateUser(navigateFromTo: (String, String) -> Unit){
        launchCatching {
            updateUserResponse = ValueOrException.Loading
            delay(1000)
            updateUserResponse = userStorageService.updateUser(User(authService.currentUserId, authService.currentUserId, uiState.value.name, uiState.value.address, uiState.value.email, uiState.value.image))
            when(updateUserResponse) {
                is ValueOrException.Success<Boolean> -> {
                    if ((updateUserResponse as ValueOrException.Success<Boolean>).data) {
                        navigateFromTo(DestinationEditProfile, DestinationProfile)
                    } else {
                        Log.d("TAG", "onUpdate: hiba")
                    }
                }
                is ValueOrException.Failure -> {
                    val exception = (updateUserResponse as ValueOrException.Failure).e
                    Log.d("TAG", "onUpdate: $exception")
                }
                else -> {
                    Log.d("TAG", "onUpdate: $updateUserResponse")
                }
            }
        }
    }
}