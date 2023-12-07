package com.example.orderapp.screens.edit_profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.orderapp.R
import com.example.orderapp.common.snackbar.SnackbarManager
import com.example.orderapp.common.utils.ValidationUtils
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.User
import com.example.orderapp.navigation.DestinationEditProfile
import com.example.orderapp.navigation.DestinationProfile
import com.example.orderapp.model.service.AuthService
import com.example.orderapp.model.service.UserStorageService
import com.example.orderapp.screens.OrderAppViewModel
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

    var userResponse by mutableStateOf<ValueOrException<User>>(ValueOrException.Loading)
        private set

    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        userResponse = ValueOrException.Loading
        launchCatching {
            delay(1000)
            userResponse = userStorageService.getUser(authService.currentUserId)
            when(userResponse){
                is ValueOrException.Success -> {
                    val data = (userResponse as ValueOrException.Success<User>).data
                    uiState.value = uiState.value.copy(
                        name = data.displayName,
                        address = data.address,
                        email = data.email,
                        image = data.image
                    )
                }
                else -> {}
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
        if (isValidProfileInputs()){
            launchCatching {
                updateUserResponse = ValueOrException.Loading
                delay(1000)
                updateUserResponse = userStorageService.updateUser(User(authService.currentUserId, authService.currentUserId, uiState.value.name, uiState.value.address, uiState.value.email, uiState.value.image))
                delay(500)
                when(updateUserResponse) {
                    is ValueOrException.Success<Boolean> -> {
                        if ((updateUserResponse as ValueOrException.Success<Boolean>).data) {
                            SnackbarManager.displayMessage(R.string.save_success)
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
        } else {
            SnackbarManager.displayMessage(R.string.invalid_customer_inputs)
        }
    }

    fun onNavigateBack(onChangesMade:()->Unit, onNoChanges:()->Unit){
        when(val userApiResponse = userResponse){
            is ValueOrException.Success -> {
                if (userApiResponse.data.image != uiState.value.image ||
                    userApiResponse.data.address != uiState.value.address ||
                    userApiResponse.data.displayName != uiState.value.name
                ){
                    onChangesMade()
                }
                else onNoChanges()
            }
            else -> Unit
        }
    }

    fun isValidProfileInputs(): Boolean {
        return when (userResponse) {
            is ValueOrException.Success -> {
                ValidationUtils.inputIsNotEmpty(uiState.value.name) &&
                        ValidationUtils.inputIsNotEmpty(uiState.value.address)
            }
            else -> {
                false
            }
        }
    }

}