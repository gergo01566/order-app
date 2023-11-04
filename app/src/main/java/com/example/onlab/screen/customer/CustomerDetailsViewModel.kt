package com.example.onlab.screen.customer

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MCustomer
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

    var customerResponse by mutableStateOf<ValueOrException<MCustomer>>(ValueOrException.Loading)
        private set

    var deleteCustomerResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var updateCustomerResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var addCustomerResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    var uiState = mutableStateOf(CustomerUiState())
        private set

    init {
        val customerId = savedStateHandle.get<String>(DestinationOneArg)
        launchCatching {
            if(!customerId.isNullOrEmpty()){
                customerResponse = storageService.getCustomer(customerId)
                when (customerResponse) {
                    is ValueOrException.Success<MCustomer> ->  {
                        val data = (customerResponse as ValueOrException.Success<MCustomer>).data
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
                customerResponse = ValueOrException.Success(MCustomer())
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

    private fun onUpdateCustomer(customer: MCustomer, navigateFromTo: (String, String) -> Unit) {
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

    private fun onAddCustomer(customer: MCustomer, navigateFromTo: (String, String) -> Unit){
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

    fun onDoneClick(customer: MCustomer, navigateFromTo: (String, String) -> Unit){
        if (customer.id!!.isBlank()){
            onAddCustomer(customer, navigateFromTo)
        } else {
            onUpdateCustomer(customer, navigateFromTo)
        }
    }
}