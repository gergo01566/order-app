package com.example.onlab.screen.customer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.onlab.data.ValueOrException
import com.example.onlab.model.MCustomer
import com.example.onlab.service.CustomerStorageService
import com.example.onlab.viewModels.OrderAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val storageService: CustomerStorageService
) : OrderAppViewModel() {

    private var searchText by mutableStateOf("")

    var customerResponse by mutableStateOf<ValueOrException<List<MCustomer>>>(ValueOrException.Loading)
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