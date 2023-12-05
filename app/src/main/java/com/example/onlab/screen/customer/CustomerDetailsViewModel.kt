package com.example.onlab.screen.customer

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.R
import com.example.onlab.components.SnackbarManager
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.Customer
import com.example.onlab.navigation.*
import com.example.onlab.service.CustomerStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    private val storageService: CustomerStorageService,
    savedStateHandle: SavedStateHandle
) : OrderAppViewModel() {

    var customerResponse by mutableStateOf<ValueOrException<Customer>>(ValueOrException.Loading)
        private set

    var deleteCustomerResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var updateCustomerResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var addCustomerResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var uiState = mutableStateOf(CustomerUiState())
        private set

    val customerId = savedStateHandle.get<String>(DestinationOneArg)


    init {
        launchCatching {
            if(!customerId.isNullOrEmpty()){
                customerResponse = storageService.getCustomer(customerId)
                when (customerResponse) {
                    is ValueOrException.Success<Customer> ->  {
                        val data = (customerResponse as ValueOrException.Success<Customer>).data
                        if (!data.id.isNullOrEmpty()){
                            uiState.value = uiState.value.copy(
                                id = data.id,
                                firstName = data.firstName,
                                lastName = data.lastName,
                                address = data.address,
                                phoneNumber = data.phoneNumber,
                                image = data.image
                            )
                        }
                    }
                    else -> {}
                }
            } else {
                customerResponse = ValueOrException.Success(Customer())
            }
        }
    }

    fun onFirstNameChange(newValue: String) {
        uiState.value = uiState.value.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        uiState.value = uiState.value.copy(lastName = newValue)
    }

    fun onAddressChange(newValue: String) {
        uiState.value = uiState.value.copy(address = newValue)
    }

    fun onPhoneNumberChange(newValue: String) {
        uiState.value = uiState.value.copy(phoneNumber = newValue)
    }

    fun onImageChange(newValue: String) {
        uiState.value = uiState.value.copy(image = newValue)
    }

    fun onDeleteCustomer(customerId: String, navigateFromTo: (String, String) -> Unit){
        launchCatching {
            deleteCustomerResponse = ValueOrException.Loading
            deleteCustomerResponse = storageService.deleteCustomer(customerId)
            when(deleteCustomerResponse) {
                is ValueOrException.Success<Boolean> -> {
                    if ((deleteCustomerResponse as ValueOrException.Success<Boolean>).data) {
                        navigateFromTo(DestinationCustomerDetails, DestinationCustomerList)
                    } else {
                        Log.d("TAG", "onDeleteProduct: hiba")
                    }
                }
                is ValueOrException.Failure -> {
                    val exception = (deleteCustomerResponse as ValueOrException.Failure).e
                    Log.d("TAG", "onDeleteCustomer: $exception")
                }
                else -> {
                    Log.d("TAG", "onDeleteCustomer: $deleteCustomerResponse")
                }
            }
        }
    }

    private fun onUpdateCustomer(customer: Customer, navigateFromTo: (String, String) -> Unit) {
        launchCatching {
            updateCustomerResponse = ValueOrException.Loading
            updateCustomerResponse = storageService.updateCustomer(customer)
            when(updateCustomerResponse) {
                is ValueOrException.Success<Boolean> -> {
                    if ((updateCustomerResponse as ValueOrException.Success<Boolean>).data) {
                        navigateFromTo(DestinationCustomerDetails, DestinationCustomerList)
                    } else {
                        Log.d("TAG", "onUpdate: hiba")
                    }
                }
                is ValueOrException.Failure -> {
                    val exception = (updateCustomerResponse as ValueOrException.Failure).e
                    Log.d("TAG", "onUpdate: $exception")
                }
                else -> {
                    Log.d("TAG", "onUpdate: $updateCustomerResponse")
                }
            }
        }
    }

    private fun onAddCustomer(customer: Customer, navigateFromTo: (String, String) -> Unit){
        launchCatching {
            addCustomerResponse = ValueOrException.Loading
            delay(500)
            addCustomerResponse = storageService.addCustomer(customer)
            when(addCustomerResponse) {
                is ValueOrException.Success<Boolean> -> {
                    if ((addCustomerResponse as ValueOrException.Success<Boolean>).data) {
                        navigateFromTo(DestinationCustomerDetails, DestinationCustomerList)
                    } else {
                        Log.d("TAG", "onSave: hiba")
                    }
                }
                is ValueOrException.Failure -> {
                    val exception = (addCustomerResponse as ValueOrException.Failure).e
                    Log.d("TAG", "onSave: $exception")
                }
                else -> {
                    Log.d("TAG", "onSave: $addCustomerResponse")
                }
            }
        }
    }

    fun onDoneClick(customer: Customer, navigateFromTo: (String, String) -> Unit){
        if (isValidCustomerInputs()){
            if (customer.id!!.isBlank()){
                onAddCustomer(customer, navigateFromTo)
            } else {
                onUpdateCustomer(customer, navigateFromTo)
            }
        } else {
            SnackbarManager.displayMessage(R.string.invalid_customer_inputs)
        }
    }

    fun onNavigateBack(onChangesMade:()->Unit, onNoChanges:()->Unit){
        Log.d("log", "onNavigateBack: $customerResponse")
        when(val customerApiResponse = customerResponse){
            is ValueOrException.Success -> {
                if (customerApiResponse.data.image != uiState.value.image ||
                    customerApiResponse.data.firstName != uiState.value.firstName ||
                    customerApiResponse.data.lastName != uiState.value.lastName ||
                    customerApiResponse.data.phoneNumber != uiState.value.phoneNumber ||
                    customerApiResponse.data.address != uiState.value.address
                ){
                    Log.d("log", "true: $customerResponse")
                    onChangesMade()
                }
                else onNoChanges()
            }
            else -> if (isValidCustomerInputs()){
                onChangesMade()
            } else {
                onNoChanges()
            }
        }
    }

    private fun isValidCustomerInputs(): Boolean{
        if (
            ValidationUtils.inputContainsOnlyLetter(uiState.value.firstName) &&
            ValidationUtils.inputContainsOnlyLetter(uiState.value.lastName) &&
            ValidationUtils.inputIsNotEmpty(uiState.value.address) &&
            ValidationUtils.inputContaintsOnlyNumbers(uiState.value.phoneNumber)
        )
            return true
        return false
    }
}

// ValidationUtils.kt
object ValidationUtils {

    fun inputContainsOnlyLetter(input: String):Boolean {
        if (!input.all { it.isLetter() } || input.isBlank()) {
            return false
        }
        return true
    }

    fun inputContaintsOnlyNumbers(input: String):Boolean {
        if (!input.all { it.isDigit() } || input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsNotEmpty(input: String):Boolean {
        if (input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsValidEmailFormat(input: String):Boolean {
        val emailRegex = Regex("[A-Za-z\\d._%+-]+@[A-Za-z\\d.-]+\\.[A-Z|a-z]{2,}")
        if (!emailRegex.matches(input) && input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsValidPrice(input: String):Boolean {
        if (input == "0" || input.isBlank()) {
            return false
        }
        return true
    }

    fun inputIsValidPassword(input: String): Boolean {
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")
        return passwordRegex.matches(input) && input.isNotBlank()
    }

}