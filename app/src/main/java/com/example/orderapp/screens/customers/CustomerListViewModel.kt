package com.example.orderapp.screens.customers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.orderapp.common.snackbar.SnackbarManager
import com.example.orderapp.model.ValueOrException
import com.example.orderapp.model.Customer
import com.example.orderapp.model.service.CustomerStorageService
import com.example.orderapp.screens.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.orderapp.R as AppText

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val storageService: CustomerStorageService
) : OrderAppViewModel() {

    private var searchText by mutableStateOf("")

    var customerResponse by mutableStateOf<ValueOrException<List<Customer>>>(ValueOrException.Loading)
        private set

    var deleteCustomerResponse by mutableStateOf<ValueOrException<Boolean>>(ValueOrException.Success(false))
        private set

    init {
        getCustomers()
    }

    private fun getCustomers() {
        customerResponse = ValueOrException.Loading
        viewModelScope.launch {
            storageService.getAllCustomers().collect { response ->
                customerResponse = response
            }
        }
        when(customerResponse){
            is ValueOrException.Failure -> SnackbarManager.displayMessage(AppText.string.data_loading_error)
            else -> Unit
        }
    }

    fun onDeleteCustomer(customerId: String, onComplete: () -> Unit){
        deleteCustomerResponse = ValueOrException.Loading
        launchCatching {
            try {
                deleteCustomerResponse = storageService.deleteCustomer(customerId)
                onComplete()
            } catch (_: Exception){

            }
        }
    }

    fun onSearchTextChanged(newText: String) {
        searchText = newText
        customerResponse = ValueOrException.Loading
        launchCatching {
            storageService.getCustomersByText(searchText).collect{ response ->
                customerResponse = response
            }
        }
    }
}